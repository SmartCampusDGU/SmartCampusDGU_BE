package org.smartcampus.smartcampus_be.domain.room.dto.request;

import org.smartcampus.smartcampus_be.domain.room.entity.Room;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomType;

import java.util.List;

public record CreateRoomRequest(
        String roomNumber,
        Long roomTypeId,
        List<RoomDataTypeRequest> dataTypes
) {
    public Room toEntity(RoomType roomType) {
        return Room.builder()
                .roomNumber(roomNumber)
                .roomType(roomType)
                .build();
    }
}
