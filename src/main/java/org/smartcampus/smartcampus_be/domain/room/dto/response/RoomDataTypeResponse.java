package org.smartcampus.smartcampus_be.domain.room.dto.response;

import lombok.Builder;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomDataThreshold;

@Builder
public record RoomDataTypeResponse(
        Long id,
        Long dataTypeId,
        String name,
        String unit,
        double cautionMin,
        double cautionMax,
        double dangerMin,
        double dangerMax,
        double emergencyMin,
        double emergencyMax,
        boolean isModified
) {
    public static RoomDataTypeResponse from(RoomDataThreshold roomDataThreshold) {
        return RoomDataTypeResponse.builder()
                .id(roomDataThreshold.getId())
                .dataTypeId(roomDataThreshold.getDataType().getId())
                .name(roomDataThreshold.getDataType().getName())
                .unit(roomDataThreshold.getDataType().getUnit())
                .cautionMin(roomDataThreshold.getCautionMin())
                .cautionMax(roomDataThreshold.getCautionMax())
                .dangerMin(roomDataThreshold.getDangerMin())
                .dangerMax(roomDataThreshold.getDangerMax())
                .emergencyMin(roomDataThreshold.getEmergencyMin())
                .emergencyMax(roomDataThreshold.getEmergencyMax())
                .isModified(roomDataThreshold.getIsModified())
                .build();
    }
}
