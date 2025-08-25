package org.smartcampus.smartcampus_be.dto;

import java.util.List;

public class CreateRoomTypeRequestDto {
    private String roomType;           // 공간유형명
    private List<SensorDto> sensors;   // 측정 항목들

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public List<SensorDto> getSensors() { return sensors; }
    public void setSensors(List<SensorDto> sensors) { this.sensors = sensors; }
}
