package org.smartcampus.smartcampus_be.domain.outlier.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class UpdateOutlierSettingsRequest {

    @Min(value = 1, message = "모니터링 지속 시간은 1분 이상이어야 합니다")
    private Integer monitoringDurationMinutes;

    @Min(value = 1, message = "중복 방지 시간은 1분 이상이어야 합니다")
    private Integer duplicatePreventionMinutes;
}