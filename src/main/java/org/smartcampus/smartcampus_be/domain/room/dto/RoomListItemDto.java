package org.smartcampus.smartcampus_be.domain.room.dto;

import java.util.List;

public class RoomListItemDto {
    private Long roomId;
    private String roomType;          // "LECTURE_ROOM" 혹은 "실험실" 등 저장된 값 그대로
    private List<String> customFields; // 커스텀 임계값을 쓰는 센서 이름 목록

    public RoomListItemDto(Long roomId, String roomType, List<String> customFields) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.customFields = customFields;
    }
    public Long getRoomId() { return roomId; }
    public String getRoomType() { return roomType; }
    public List<String> getCustomFields() { return customFields; }
}
