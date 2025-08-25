package org.smartcampus.smartcampus_be.domain.room.service;

import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.room.dto.*;
import org.smartcampus.smartcampus_be.domain.room.dto.RoomDetailDto.MeasurementDto;
import org.smartcampus.smartcampus_be.domain.room.entity.Room;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomSensor;
import org.smartcampus.smartcampus_be.domain.room.repository.RoomRepository;
import org.smartcampus.smartcampus_be.global.exception.CustomException;
import org.smartcampus.smartcampus_be.global.exception.ErrorType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional
    public Long createRoom(CreateRoomRequestDto request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new CustomException(ErrorType.DUPLICATE_ROOM_NUMBER);
        }

        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .roomType(request.getRoomType())
                .build();

        for (var s : request.getSensors()) {
            if (!s.isUseDefault() && s.getThresholds() == null) {
                throw new CustomException(ErrorType.INVALID_THRESHOLD_REQUEST);
            }

            RoomSensor.RoomSensorBuilder sensorBuilder = RoomSensor.builder()
                    .name(s.getName())
                    .useDefault(s.isUseDefault());

            if (!s.isUseDefault() && s.getThresholds() != null) {
                var t = s.getThresholds();
                if (t.getCaution() != null) {
                    sensorBuilder.cautionMin(toDouble(t.getCaution().getMin()))
                               .cautionMax(toDouble(t.getCaution().getMax()));
                }
                if (t.getDanger() != null) {
                    sensorBuilder.dangerMin(toDouble(t.getDanger().getMin()))
                               .dangerMax(toDouble(t.getDanger().getMax()));
                }
                if (t.getEmergency() != null) {
                    sensorBuilder.emergencyMin(toDouble(t.getEmergency().getMin()))
                               .emergencyMax(toDouble(t.getEmergency().getMax()));
                }
            }

            RoomSensor sensor = sensorBuilder.build();
            room.addSensor(sensor);
        }

        Room saved = roomRepository.save(room);
        return saved.getId();
    }

    @Transactional(readOnly = true)
    public List<RoomListItemDto> getRooms(String roomType) {
        List<Room> rooms = (roomType == null || roomType.isBlank())
                ? roomRepository.findAll()
                : roomRepository.findByRoomType(roomType);

        return rooms.stream()
                .map(room -> new RoomListItemDto(
                        room.getId(),
                        room.getRoomType(),
                        room.getSensors().stream()
                                .filter(s -> !toBool(s.isUseDefault()))
                                .map(RoomSensor::getName)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoomDetailDto getRoomDetail(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorType.ROOM_NOT_FOUND));
        return buildDetail(room);
    }

    @Transactional
    public RoomDetailDto updateRoom(Long roomId, UpdateRoomRequestDto request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorType.ROOM_NOT_FOUND));

        room.setRoomType(request.getRoomType());

        room.getSensors().clear();
        for (var m : request.getMeasurements()) {
            RoomSensor.RoomSensorBuilder sensorBuilder = RoomSensor.builder()
                    .room(room)
                    .name(m.getName());

            if (m.getUnit() != null && !m.getUnit().isBlank()) {
                sensorBuilder.unit(m.getUnit());
            }

            var th = m.getThresholds();
            if (th == null) {
                sensorBuilder.useDefault(true);
            } else {
                sensorBuilder.useDefault(false);
                
                List<Double> caution = th.get("주의");
                if (caution != null && caution.size() >= 2) {
                    sensorBuilder.cautionMin(caution.get(0)).cautionMax(caution.get(1));
                }
                
                List<Double> danger = th.get("위험");
                if (danger != null && danger.size() >= 2) {
                    sensorBuilder.dangerMin(danger.get(0)).dangerMax(danger.get(1));
                }
                
                List<Double> emergency = th.get("응급");
                if (emergency != null && emergency.size() >= 2) {
                    sensorBuilder.emergencyMin(emergency.get(0)).emergencyMax(emergency.get(1));
                }
            }
            
            RoomSensor sensor = sensorBuilder.build();
            room.addSensor(sensor);
        }

        Room saved = roomRepository.save(room);
        return buildDetail(saved);
    }

    private boolean toBool(Boolean b) { 
        return b != null && b; 
    }

    private static Double toDouble(Number n) { 
        return (n == null) ? null : n.doubleValue(); 
    }
    private RoomDetailDto buildDetail(Room room) {
        List<MeasurementDto> measurements = room.getSensors().stream()
                .map(s -> {
                    String unit = s.getUnit() != null ? s.getUnit() : "";
                    Map<String, List<Double>> thresholds = new LinkedHashMap<>();
                    thresholds.put("주의", Arrays.asList(s.getCautionMin(), s.getCautionMax()));
                    thresholds.put("위험", Arrays.asList(s.getDangerMin(), s.getDangerMax()));
                    thresholds.put("응급", Arrays.asList(s.getEmergencyMin(), s.getEmergencyMax()));
                    return new MeasurementDto(s.getName(), unit, thresholds);
                })
                .collect(Collectors.toList());

        return new RoomDetailDto(room.getId(), room.getRoomType(), measurements);
    }

}
