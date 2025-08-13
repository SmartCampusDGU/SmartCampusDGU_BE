package org.smartcampus.smartcampus_be.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public class SensorDto {

    @NotBlank
    private String name;          // "온도", "습도"
    private boolean useDefault;   // 기본값 사용 여부

    @Valid
    private ThresholdDto thresholds; // useDefault=false일 때만 필요

    // getters/setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isUseDefault() { return useDefault; }
    public void setUseDefault(boolean useDefault) { this.useDefault = useDefault; }
    public ThresholdDto getThresholds() { return thresholds; }
    public void setThresholds(ThresholdDto thresholds) { this.thresholds = thresholds; }
}
