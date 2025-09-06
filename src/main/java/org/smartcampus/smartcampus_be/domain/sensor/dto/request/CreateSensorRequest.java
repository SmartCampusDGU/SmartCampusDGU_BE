package org.smartcampus.smartcampus_be.domain.sensor.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.smartcampus.smartcampus_be.domain.room.entity.Room;
import org.smartcampus.smartcampus_be.domain.sensor.entity.Sensor;

public record CreateSensorRequest(
        @NotNull
        Long roomId,
        @NotBlank
        String macAddress
) {
    public Sensor toEntity(Room room) {
        return Sensor.builder()
                .room(room)
                .macAddress(macAddress)
                .build();
    }
}
