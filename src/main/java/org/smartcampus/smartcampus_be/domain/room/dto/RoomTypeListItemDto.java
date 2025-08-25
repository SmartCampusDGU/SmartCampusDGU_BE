package org.smartcampus.smartcampus_be.domain.room.dto;

import java.util.List;

public class RoomTypeListItemDto {
    private String roomType;
    private List<String> sensorTypes;

    public RoomTypeListItemDto(String roomType, List<String> sensorTypes) {
        this.roomType = roomType;
        this.sensorTypes = sensorTypes;
    }
    
    public String getRoomType() { return roomType; }
    public List<String> getSensorTypes() { return sensorTypes; }
}
