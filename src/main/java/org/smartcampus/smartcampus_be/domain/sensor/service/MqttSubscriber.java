package org.smartcampus.smartcampus_be.domain.sensor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartcampus.smartcampus_be.domain.outlier.service.OutlierService;
import org.smartcampus.smartcampus_be.domain.sensor.entity.DataType;
import org.smartcampus.smartcampus_be.domain.sensor.entity.Sensor;
import org.smartcampus.smartcampus_be.domain.sensor.entity.SensorData;
import org.smartcampus.smartcampus_be.domain.sensor.repository.DataTypeRepository;
import org.smartcampus.smartcampus_be.domain.sensor.repository.SensorDataRepository;
import org.smartcampus.smartcampus_be.domain.sensor.repository.SensorRepository;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MqttSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(MqttSubscriber.class);
    private static final String clientId = "spring-mqtt-client";
    private static final String TOPIC_FILTER = "meraki/v1/mt/+/ble/+/#";
    private final DataTypeRepository dataTypeRepository;
    private final SensorRepository sensorRepository;
    private final SensorDataRepository sensorDataRepository;
    private final OutlierService outlierService;

    @Value("${mqtt.broker-url}")
    private String BROKER_URL;

    @Value("${mqtt.username:}")
    private String MQTT_USERNAME;

    @Value("${mqtt.password:}")
    private String MQTT_PASSWORD;

    private MqttClient mqttClient;

    @PostConstruct
    public void init() {
        try {
            mqttClient = new MqttClient(BROKER_URL, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            if (MQTT_USERNAME != null && !MQTT_USERNAME.isEmpty()) {
                options.setUserName(MQTT_USERNAME);
            }
            if (MQTT_PASSWORD != null && !MQTT_PASSWORD.isEmpty()) {
                options.setPassword(MQTT_PASSWORD.toCharArray());
            }

            mqttClient.connect(options);
            logger.info("MQTT 브로커에 연결 성공: {}", BROKER_URL);

            subscribe();
        } catch (MqttException e) {
            logger.error("MQTT 연결 실패: {}", e.getMessage(), e);
        }
    }

    public void subscribe() throws MqttException {
        mqttClient.subscribe(TOPIC_FILTER, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                try {
                    String payload = new String(message.getPayload());
                    logger.info("메시지 도착 - 토픽: {}, 페이로드: {}", topic, payload);
                    processMessage(topic, payload);
                } catch (Exception e) {
                    logger.error("메시지 처리 중 오류 발생: {}", e.getMessage(), e);
                }
            }
        });
        logger.info("MQTT 구독 시작: {}", TOPIC_FILTER);
    }

    @Transactional
    public void processMessage(String topic, String payload) throws Exception {
        String dataTypeName = topic.substring(topic.lastIndexOf('/') + 1);
        DataType dataType = dataTypeRepository.findByName(dataTypeName).orElseGet(() -> {
            logger.debug("데이터 타입이 데이터베이스에 없음: {}", dataTypeName);
            DataType newDataType = DataType.builder()
                    .name(dataTypeName)
                    .unit(guessUnit(dataTypeName))
                    .build();
            return dataTypeRepository.save(newDataType);
        });

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(payload);

        String macAddress = extractMacAddressFromTopic(topic);
        Optional<Sensor> sensorOpt = sensorRepository.findByMacAddress(macAddress);
        if (sensorOpt.isEmpty()) {
            logger.warn("미등록 센서에서 메시지 수신 - MAC: {}, 토픽: {}", macAddress, topic);
            return; // 메시지 무시하고 다음 메시지 처리 계속
        }
        Sensor sensor = sensorOpt.get();
        LocalDateTime timestamp = parseTimestamp(jsonNode.get("ts").asText());
        String value = getValueFromJsonNode(dataTypeName, jsonNode);

        SensorData sensorData = SensorData.builder()
                .sensor(sensor)
                .dataType(dataType)
                .value(value)
                .createdAt(timestamp)
                .build();

        sensorDataRepository.save(sensorData);

        // 이상치 감지 및 저장
        outlierService.detectAndSaveOutlier(sensorData);

        logger.info("{} data saved: {}", dataType, sensorData);
    }

    private String extractMacAddressFromTopic(String topic) {
        String[] parts = topic.split("/");
        String macAddress = parts[5];
        logger.info("센서 MAC 추출: {}", macAddress);
        return macAddress;
    }

    private LocalDateTime parseTimestamp(String timestampStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("Asia/Seoul"));
        Instant instant = Instant.from(formatter.parse(timestampStr));
        return LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul"));
    }

    private String getValueFromJsonNode(String dataTypeName, JsonNode jsonNode) {
        switch (dataTypeName) {
            case "temperature":
                return jsonNode.get("celsius").toString();
            case "tvoc":
                return jsonNode.get("tvoc").toString();
            case "ambientNoise":
                return jsonNode.get("ambientNoise").toString();
            case "iaqIndex":
                return jsonNode.get("iaqIndex").toString();
//            case "aqmScores":
//                return jsonNode.toString();
            case "humidity":
                return jsonNode.get("humidity").toString();
            case "usbPowered":
                return jsonNode.get("usbPowered").toString();
            case "buttonPressed":
                return jsonNode.get("buttonPressed").toString();
            case "PM2_5MassConcentration":
                return jsonNode.get("PM2_5MassConcentration").toString();
            case "missedConnections":
                return jsonNode.get("missedConnections").toString();
            default:
                logger.warn("알 수 없는 센서 타입: {}", dataTypeName);
                return jsonNode.toString(); // 전체 JSON을 문자열로 저장
        }
    }

    private String guessUnit(String type) {
        return switch (type) {
            case "humidity", "batteryPercentage" -> "%";
            case "temperature" -> "°C";
            case "tvoc" -> "ug/m³";
            case "ambientNoise" -> "dBA";
            case "iaqIndex" -> "index";
            case "usbPowered", "buttonPressed" -> "boolean";
            case "PM2_5MassConcentration" -> "µg/m³";
            case "missedConnections" -> "count";
            default -> "unknown";
        };
    }

}
