package org.smartcampus.smartcampus_be.domain.sensor.service;

import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.room.entity.Room;
import org.smartcampus.smartcampus_be.domain.room.repository.RoomRepository;
import org.smartcampus.smartcampus_be.domain.sensor.dto.request.CreateSensorRequest;
import org.smartcampus.smartcampus_be.domain.sensor.dto.request.DeleteSensorRequest;
import org.smartcampus.smartcampus_be.domain.sensor.dto.response.PagingSensorResponse;
import org.smartcampus.smartcampus_be.domain.sensor.dto.response.SensorResponse;
import org.smartcampus.smartcampus_be.domain.sensor.entity.Sensor;
import org.smartcampus.smartcampus_be.domain.sensor.repository.SensorRepository;
import org.smartcampus.smartcampus_be.global.exception.CustomException;
import org.smartcampus.smartcampus_be.global.exception.ErrorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SensorService {

    private final RoomRepository roomRepository;
    private final SensorRepository sensorRepository;

    @Transactional
    public SensorResponse createSensor(CreateSensorRequest request) {
        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new CustomException(ErrorType.ROOM_NOT_FOUND));
        Sensor sensor = request.toEntity(room);
        Sensor savedSensor = sensorRepository.save(sensor);
        return SensorResponse.from(savedSensor);
    }

    public PagingSensorResponse getAllSensors(String roomNumber, Pageable pageable) {
        Page<Sensor> sensorPage;
        if (roomNumber != null && !roomNumber.isEmpty()) {
            Room room = roomRepository.findByRoomNumber(roomNumber)
                    .orElseThrow(() -> new CustomException(ErrorType.ROOM_NOT_FOUND));
            sensorPage = sensorRepository.findByRoom(room, pageable);
        } else {
            sensorPage = sensorRepository.findAll(pageable);
        }
        List<SensorResponse> sensors = sensorPage.getContent().stream()
                .map(SensorResponse::from)
                .collect(Collectors.toList());
        return PagingSensorResponse.of(sensorPage, sensors);
    }

    @Transactional
    public void deleteSensor(Long sensorId, DeleteSensorRequest request) {
        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new CustomException(ErrorType.SENSOR_NOT_FOUND));
        if (request.deleteReason() != null) {
            sensor.updateDeleteReason(request.deleteReason());
        }
        sensorRepository.delete(sensor);
    }
}
