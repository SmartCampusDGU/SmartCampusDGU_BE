package org.smartcampus.smartcampus_be.domain.sensor.dto.request;

public record DeleteSensorRequest(
        String roomNumber,
        String macAddress,
        String deleteReason
) {
}
