package org.smartcampus.smartcampus_be.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class CreateRoomRequestDto {

    @NotBlank
    private String roomNumber;   // "1103"

    @NotBlank
    private String roomType;     // "실험실"

    @NotEmpty
    private List<@Valid SensorDto> sensors;

    // getters/setters
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public List<SensorDto> getSensors() { return sensors; }
    public void setSensors(List<SensorDto> sensors) { this.sensors = sensors; }
}
