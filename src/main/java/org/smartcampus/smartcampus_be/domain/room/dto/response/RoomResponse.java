package org.smartcampus.smartcampus_be.domain.room.dto.response;

import lombok.Builder;
import org.smartcampus.smartcampus_be.domain.room.entity.Room;

import java.util.List;

@Builder
public record RoomResponse(
        Long id,
        String roomNumber,
        Long roomTypeId,
        String roomType,
        List<RoomDataTypeResponse> dataTypes
) {
    public static RoomResponse from(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .roomTypeId(room.getRoomType().getId())
                .roomType(room.getRoomType().getName())
                .dataTypes(room.getRoomDataThresholds().stream()
                        .map(RoomDataTypeResponse::from)
                        .toList())
                .build();
    }
}
