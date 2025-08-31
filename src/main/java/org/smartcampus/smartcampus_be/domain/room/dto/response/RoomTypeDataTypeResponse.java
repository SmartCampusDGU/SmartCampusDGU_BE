package org.smartcampus.smartcampus_be.domain.room.dto.response;

import lombok.Builder;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomTypeDataThreshold;

@Builder
public record RoomTypeDataTypeResponse(
        Long id,
        Long dataTypeId,
        String name,
        String unit,
        double cautionMin,
        double cautionMax,
        double dangerMin,
        double dangerMax,
        double emergencyMin,
        double emergencyMax
) {
    public static RoomTypeDataTypeResponse from(RoomTypeDataThreshold roomTypeDataThreshold) {
        return RoomTypeDataTypeResponse.builder()
                .id(roomTypeDataThreshold.getId())
                .dataTypeId(roomTypeDataThreshold.getDataType().getId())
                .name(roomTypeDataThreshold.getDataType().getName())
                .unit(roomTypeDataThreshold.getDataType().getUnit())
                .cautionMin(roomTypeDataThreshold.getCautionMin())
                .cautionMax(roomTypeDataThreshold.getCautionMax())
                .dangerMin(roomTypeDataThreshold.getDangerMin())
                .dangerMax(roomTypeDataThreshold.getDangerMax())
                .emergencyMin(roomTypeDataThreshold.getEmergencyMin())
                .emergencyMax(roomTypeDataThreshold.getEmergencyMax())
                .build();
    }
}
