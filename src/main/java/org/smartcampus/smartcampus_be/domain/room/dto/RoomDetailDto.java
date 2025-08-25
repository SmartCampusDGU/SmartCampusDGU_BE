package org.smartcampus.smartcampus_be.domain.room.dto;

import java.util.List;
import java.util.Map;

public class RoomDetailDto {
    private Long roomId;
    private String roomType;
    private List<MeasurementDto> measurements;

    public RoomDetailDto(Long roomId, String roomType, List<MeasurementDto> measurements) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.measurements = measurements;
    }
    public Long getRoomId() { return roomId; }
    public String getRoomType() { return roomType; }
    public List<MeasurementDto> getMeasurements() { return measurements; }

    // measurements[] 아이템
    public static class MeasurementDto {
        private String name;                 // 측정 항목명 (예: 온도)
        private String unit;                 // 단위 (예: °C, %, ppb) — 없으면 빈 문자열
        private Map<String, List<Double>> thresholds; // {"주의":[min,max], "위험":[min,max], "응급":[min,max]}

        public MeasurementDto(String name, String unit, Map<String, List<Double>> thresholds) {
            this.name = name;
            this.unit = unit;
            this.thresholds = thresholds;
        }
        public String getName() { return name; }
        public String getUnit() { return unit; }
        public Map<String, List<Double>> getThresholds() { return thresholds; }
    }
}
