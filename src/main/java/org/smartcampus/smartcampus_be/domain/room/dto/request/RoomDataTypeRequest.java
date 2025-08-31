package org.smartcampus.smartcampus_be.domain.room.dto.request;

import org.smartcampus.smartcampus_be.domain.room.entity.Room;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomDataThreshold;
import org.smartcampus.smartcampus_be.domain.sensor.entity.DataType;

public record RoomDataTypeRequest(
        Long id,
        double cautionMin,
        double cautionMax,
        double dangerMin,
        double dangerMax,
        double emergencyMin,
        double emergencyMax,
        boolean isModified
) {
    public RoomDataThreshold toEntity(Room room, DataType dataType) {
        return RoomDataThreshold.builder()
                .room(room)
                .dataType(dataType)
                .cautionMin(cautionMin)
                .cautionMax(cautionMax)
                .dangerMin(dangerMin)
                .dangerMax(dangerMax)
                .emergencyMin(emergencyMin)
                .emergencyMax(emergencyMax)
                .isModified(isModified)
                .build();
    }
}
