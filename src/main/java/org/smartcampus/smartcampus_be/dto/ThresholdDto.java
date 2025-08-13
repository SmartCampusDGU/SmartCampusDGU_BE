package org.smartcampus.smartcampus_be.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class ThresholdDto {

    @JsonProperty("주의")
    private Range caution;

    @JsonProperty("위험")
    private Range danger;

    @JsonProperty("응급")
    private Range emergency;

    // 필요 시 각 Range에 @NotNull 추가 가능 (정책에 따라)
    public static class Range {
        @NotNull
        private Integer min;
        @NotNull
        private Integer max;

        public Integer getMin() { return min; }
        public void setMin(Integer min) { this.min = min; }
        public Integer getMax() { return max; }
        public void setMax(Integer max) { this.max = max; }
    }

    // getters/setters
    public Range getCaution() { return caution; }
    public void setCaution(Range caution) { this.caution = caution; }
    public Range getDanger() { return danger; }
    public void setDanger(Range danger) { this.danger = danger; }
    public Range getEmergency() { return emergency; }
    public void setEmergency(Range emergency) { this.emergency = emergency; }
}
