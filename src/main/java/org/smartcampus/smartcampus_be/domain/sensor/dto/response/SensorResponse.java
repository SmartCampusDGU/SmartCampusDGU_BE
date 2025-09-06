package org.smartcampus.smartcampus_be.domain.sensor.dto.response;

import lombok.Builder;
import org.smartcampus.smartcampus_be.domain.sensor.entity.Sensor;

@Builder
public record SensorResponse(
        String roomNumber,
        String macAddress
) {
    public static SensorResponse from(Sensor sensor) {
        return SensorResponse.builder()
                .roomNumber(sensor.getRoom().getRoomNumber())
                .macAddress(sensor.getMacAddress())
                .build();
    }
}
