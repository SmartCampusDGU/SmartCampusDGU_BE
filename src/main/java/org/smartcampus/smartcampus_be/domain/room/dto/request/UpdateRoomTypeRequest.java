package org.smartcampus.smartcampus_be.domain.room.dto.request;

import java.util.List;

public record UpdateRoomTypeRequest(
        String name,
        String description,
        List<RoomTypeDataTypeRequest> dataTypes
) {
}
