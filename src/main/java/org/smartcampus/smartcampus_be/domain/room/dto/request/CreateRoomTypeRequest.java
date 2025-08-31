package org.smartcampus.smartcampus_be.domain.room.dto.request;

import jakarta.validation.constraints.NotEmpty;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomType;

import java.util.List;

public record CreateRoomTypeRequest(
        String name,
        String description,
        @NotEmpty
        List<RoomTypeDataTypeRequest> dataTypes
) {
    public RoomType toEntity() {
        return RoomType.builder()
                .name(name)
                .description(description)
                .build();
    }
}
