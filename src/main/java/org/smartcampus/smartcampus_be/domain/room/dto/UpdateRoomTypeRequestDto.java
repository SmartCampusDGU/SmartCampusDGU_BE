package org.smartcampus.smartcampus_be.domain.room.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class UpdateRoomTypeRequestDto {

    @NotEmpty
    @Valid
    private List<SensorDto> measurements;

    public List<SensorDto> getMeasurements() { return measurements; }
    public void setMeasurements(List<SensorDto> measurements) { this.measurements = measurements; }
}
