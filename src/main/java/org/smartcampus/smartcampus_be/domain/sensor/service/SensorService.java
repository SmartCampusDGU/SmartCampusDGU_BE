package org.smartcampus.smartcampus_be.domain.sensor.service;

import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.room.entity.Room;
import org.smartcampus.smartcampus_be.domain.room.repository.RoomRepository;
import org.smartcampus.smartcampus_be.domain.sensor.dto.request.CreateSensorRequest;
import org.smartcampus.smartcampus_be.domain.sensor.dto.response.SensorResponse;
import org.smartcampus.smartcampus_be.domain.sensor.entity.Sensor;
import org.smartcampus.smartcampus_be.domain.sensor.repository.SensorRepository;
import org.smartcampus.smartcampus_be.global.exception.CustomException;
import org.smartcampus.smartcampus_be.global.exception.ErrorType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
