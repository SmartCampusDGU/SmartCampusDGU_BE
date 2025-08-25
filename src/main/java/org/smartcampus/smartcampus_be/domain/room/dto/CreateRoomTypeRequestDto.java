package org.smartcampus.smartcampus_be.domain.room.dto;

import java.util.List;

public class CreateRoomTypeRequestDto {
    private String roomType;
    private List<SensorDto> sensors;

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public List<SensorDto> getSensors() { return sensors; }
    public void setSensors(List<SensorDto> sensors) { this.sensors = sensors; }
}
