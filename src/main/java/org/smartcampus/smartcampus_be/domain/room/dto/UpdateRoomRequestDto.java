package org.smartcampus.smartcampus_be.domain.room.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public class UpdateRoomRequestDto {
    @NotBlank
    private String roomType;                 // 변경할 공간 타입

    @NotNull
    private List<UpdateMeasurementDto> measurements; // 최종 측정 항목 리스트(치환)

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public List<UpdateMeasurementDto> getMeasurements() { return measurements; }
    public void setMeasurements(List<UpdateMeasurementDto> measurements) { this.measurements = measurements; }

    // measurements 배열의 원소
    public static class UpdateMeasurementDto {
        @NotBlank
        private String name;                         // 측정 항목명
        private String unit;                         // Optional
        // {"주의":[min,max], "위험":[min,max], "응급":[min,max]} — 각 값 null 허용
        private Map<String, List<Double>> thresholds; // Optional

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        public Map<String, List<Double>> getThresholds() { return thresholds; }
        public void setThresholds(Map<String, List<Double>> thresholds) { this.thresholds = thresholds; }
    }
}
