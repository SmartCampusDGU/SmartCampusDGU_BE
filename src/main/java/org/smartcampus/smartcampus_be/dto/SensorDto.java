package org.smartcampus.smartcampus_be.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public class SensorDto {

    @NotBlank
    private String name;          // "온도", "습도"
    private boolean useDefault;   // 기본값 사용 여부
    // PATCH에서 필요하지만, POST에선 없어도 되므로 '선택' 값
    private String unit;  //"°C","%"
    @Valid
    private ThresholdDto thresholds; // useDefault=false일 때만 필요

    // getters/setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isUseDefault() { return useDefault; }
    public void setUseDefault(boolean useDefault) { this.useDefault = useDefault; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public ThresholdDto getThresholds() { return thresholds; }
    public void setThresholds(ThresholdDto thresholds) { this.thresholds = thresholds; }
}
