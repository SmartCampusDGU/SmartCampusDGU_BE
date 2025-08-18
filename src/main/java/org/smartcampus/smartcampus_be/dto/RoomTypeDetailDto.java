package org.smartcampus.smartcampus_be.dto;

import java.util.List;

public class RoomTypeDetailDto {
    private String roomType;
    private List<SensorDto> measurements;

    public RoomTypeDetailDto(String roomType, List<SensorDto> measurements) {
        this.roomType = roomType;
        this.measurements = measurements;
    }

    public String getRoomType() { return roomType; }
    public List<SensorDto> getMeasurements() { return measurements; }
}
