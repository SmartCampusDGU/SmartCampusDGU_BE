package org.smartcampus.smartcampus_be.domain.room.dto.request;

import java.util.List;

public record UpdateRoomRequest(
        String roomNumber,
        Long roomTypeId,
        List<RoomDataTypeRequest> dataTypes
) {
}
