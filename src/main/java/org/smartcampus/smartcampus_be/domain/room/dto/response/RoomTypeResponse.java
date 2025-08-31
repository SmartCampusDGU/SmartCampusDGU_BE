package org.smartcampus.smartcampus_be.domain.room.dto.response;

import lombok.Builder;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomType;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record RoomTypeResponse(
        Long id,
        String name,
        String description,
        List<RoomTypeDataTypeResponse> dataTypes
) {
    public static RoomTypeResponse from(RoomType roomType) {
        return RoomTypeResponse.builder()
                .id(roomType.getId())
                .name(roomType.getName())
                .description(roomType.getDescription())
                .dataTypes(roomType.getRoomTypeDataThresholds().stream()
                        .map(RoomTypeDataTypeResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
