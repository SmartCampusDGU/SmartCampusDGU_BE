package org.smartcampus.smartcampus_be.dto;

import java.util.List;

public class RoomTypeListItemDto {
    private String roomType;          // 공간유형명
    private List<String> sensorTypes; // 해당 유형의 센서 이름 목록

    public RoomTypeListItemDto(String roomType, List<String> sensorTypes) {
        this.roomType = roomType;
        this.sensorTypes = sensorTypes;
    }
    public String getRoomType() { return roomType; }
    public List<String> getSensorTypes() { return sensorTypes; }
}
