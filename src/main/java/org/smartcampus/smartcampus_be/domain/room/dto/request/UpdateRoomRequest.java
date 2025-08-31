package org.smartcampus.smartcampus_be.domain.room.dto.request;

import java.util.List;

public record UpdateRoomRequest(
        List<RoomDataTypeRequest> dataTypes
) {
}
