package org.smartcampus.smartcampus_be.domain.sensor.dto.response;

import lombok.Builder;
import org.smartcampus.smartcampus_be.domain.sensor.entity.Sensor;
import org.smartcampus.smartcampus_be.global.common.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PagingSensorResponse(
        PageResponse page,
        List<SensorResponse> sensors
) {
    public static PagingSensorResponse of(Page<Sensor> sensorPage, List<SensorResponse> sensors) {
        return PagingSensorResponse.builder()
                .page(PageResponse.from(sensorPage))
                .sensors(sensors)
                .build();
    }
}
