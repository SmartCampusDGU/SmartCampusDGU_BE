package org.smartcampus.smartcampus_be.domain.room.service;

import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.room.dto.*;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomType;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomTypeDataThreshold;
import org.smartcampus.smartcampus_be.domain.room.repository.RoomRepository;
import org.smartcampus.smartcampus_be.domain.room.repository.RoomTypeRepository;
import org.smartcampus.smartcampus_be.global.exception.CustomException;
import org.smartcampus.smartcampus_be.global.exception.ErrorType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public Long createRoomType(CreateRoomTypeRequestDto request) {
        if (request == null || request.getRoomType() == null || request.getRoomType().isBlank()) {
            throw new CustomException(ErrorType.REQUIRED_VALUE_MISSING);
        }
        if (roomTypeRepository.existsByName(request.getRoomType())) {
            throw new CustomException(ErrorType.DUPLICATE_ROOM_TYPE);
        }
        if (request.getSensors() == null || request.getSensors().isEmpty()) {
            throw new CustomException(ErrorType.SENSOR_REQUIRED);
        }

        RoomType roomType = RoomType.builder()
                .name(request.getRoomType())
                .build();

        for (SensorDto s : request.getSensors()) {
            if (s.getName() == null || s.getName().isBlank()) {
                throw new CustomException(ErrorType.SENSOR_NAME_REQUIRED);
            }

            RoomTypeDataThreshold.RoomTypeSensorBuilder sensorBuilder = RoomTypeDataThreshold.builder()
                    .name(s.getName())
                    .unit(s.getUnit());

            if (!s.isUseDefault()) {
                ThresholdDto th = s.getThresholds();
                if (th == null) {
                    throw new CustomException(ErrorType.THRESHOLDS_REQUIRED);
                }

                if (th.getCaution() != null) {
                    sensorBuilder.cautionMin(toDouble(th.getCaution().getMin()))
                               .cautionMax(toDouble(th.getCaution().getMax()));
                }
                if (th.getDanger() != null) {
                    sensorBuilder.dangerMin(toDouble(th.getDanger().getMin()))
                               .dangerMax(toDouble(th.getDanger().getMax()));
                }
                if (th.getEmergency() != null) {
                    sensorBuilder.emergencyMin(toDouble(th.getEmergency().getMin()))
                               .emergencyMax(toDouble(th.getEmergency().getMax()));
                }
            }

            RoomTypeDataThreshold sensor = sensorBuilder.build();
            roomType.addSensor(sensor);
        }

        RoomType savedRoomType = roomTypeRepository.save(roomType);
        return savedRoomType.getId();
    }

    @Transactional(readOnly = true)
    public List<RoomTypeListItemDto> getRoomTypes() {
        return roomTypeRepository.findAll().stream()
                .map(rt -> new RoomTypeListItemDto(
                        rt.getName(),
                        rt.getSensors().stream()
                                .map(RoomTypeDataThreshold::getName)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRoomType(Long id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorType.ROOM_TYPE_NOT_FOUND));

        boolean inUse = !roomRepository.findByRoomType(roomType.getName()).isEmpty();
        if (inUse) {
            throw new CustomException(ErrorType.ROOM_TYPE_IN_USE);
        }

        roomTypeRepository.delete(roomType);
    }

    @Transactional
    public RoomTypeDetailDto updateRoomType(Long id, UpdateRoomTypeRequestDto request) {
        if (request == null || request.getMeasurements() == null) {
            throw new CustomException(ErrorType.REQUIRED_VALUE_MISSING);
        }

        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorType.ROOM_TYPE_NOT_FOUND));

        for (SensorDto m : request.getMeasurements()) {
            if (m.getName() == null || m.getName().isBlank()) {
                throw new CustomException(ErrorType.SENSOR_NAME_REQUIRED);
            }

            RoomTypeDataThreshold target = roomType.getSensors().stream()
                    .filter(s -> m.getName().equals(s.getName()))
                    .findFirst()
                    .orElse(null);

            if (target == null) {
                target = RoomTypeDataThreshold.builder()
                        .name(m.getName())
                        .build();
                roomType.addSensor(target);
            }

            if (m.getUnit() != null && !m.getUnit().isBlank()) {
                target.setUnit(m.getUnit());
            }

            if (m.getThresholds() != null) {
                ThresholdDto t = m.getThresholds();
                if (t.getCaution() != null) {
                    target.setCautionMin(toDouble(t.getCaution().getMin()));
                    target.setCautionMax(toDouble(t.getCaution().getMax()));
                }
                if (t.getDanger() != null) {
                    target.setDangerMin(toDouble(t.getDanger().getMin()));
                    target.setDangerMax(toDouble(t.getDanger().getMax()));
                }
                if (t.getEmergency() != null) {
                    target.setEmergencyMin(toDouble(t.getEmergency().getMin()));
                    target.setEmergencyMax(toDouble(t.getEmergency().getMax()));
                }
            }
        }

        RoomType savedRoomType = roomTypeRepository.save(roomType);

        List<SensorDto> measurements = new ArrayList<>();
        for (RoomTypeDataThreshold s : savedRoomType.getSensors()) {
            SensorDto d = new SensorDto();
            d.setName(s.getName());
            d.setUnit(s.getUnit());

            ThresholdDto t = new ThresholdDto();

            ThresholdDto.Range c = new ThresholdDto.Range();
            c.setMin(toInteger(s.getCautionMin()));
            c.setMax(toInteger(s.getCautionMax()));
            t.setCaution(c);

            ThresholdDto.Range g = new ThresholdDto.Range();
            g.setMin(toInteger(s.getDangerMin()));
            g.setMax(toInteger(s.getDangerMax()));
            t.setDanger(g);

            ThresholdDto.Range e = new ThresholdDto.Range();
            e.setMin(toInteger(s.getEmergencyMin()));
            e.setMax(toInteger(s.getEmergencyMax()));
            t.setEmergency(e);

            d.setThresholds(t);
            measurements.add(d);
        }

        return new RoomTypeDetailDto(savedRoomType.getName(), measurements);
    }

    private Double toDouble(Integer value) {
        return (value == null) ? null : value.doubleValue();
    }

    private Integer toInteger(Double value) {
        return (value == null) ? null : value.intValue();
    }
}