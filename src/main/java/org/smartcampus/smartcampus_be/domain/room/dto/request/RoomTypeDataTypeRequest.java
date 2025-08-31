package org.smartcampus.smartcampus_be.domain.room.dto.request;

import org.smartcampus.smartcampus_be.domain.room.entity.RoomType;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomTypeDataThreshold;
import org.smartcampus.smartcampus_be.domain.sensor.entity.DataType;

public record RoomTypeDataTypeRequest(
        Long id,
        double cautionMin,
        double cautionMax,
        double dangerMin,
        double dangerMax,
        double emergencyMin,
        double emergencyMax
) {
    public RoomTypeDataThreshold toEntity(RoomType roomType, DataType dataType) {
        return RoomTypeDataThreshold.builder()
                .roomType(roomType)
                .dataType(dataType)
                .cautionMin(cautionMin)
                .cautionMax(cautionMax)
                .dangerMin(dangerMin)
                .dangerMax(dangerMax)
                .emergencyMin(emergencyMin)
                .emergencyMax(emergencyMax)
                .build();
    }
}
