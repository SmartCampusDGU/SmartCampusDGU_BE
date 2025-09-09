package org.smartcampus.smartcampus_be.domain.sensor.dto.response;

import lombok.Builder;
import org.smartcampus.smartcampus_be.domain.sensor.entity.DataType;

@Builder
public record DataTypeResponse(
        Long id,
        String name,
        String unit
) {
    public static DataTypeResponse from(DataType dataType) {
        return DataTypeResponse.builder()
                .id(dataType.getId())
                .name(dataType.getName())
                .unit(dataType.getUnit())
                .build();
    }
}
