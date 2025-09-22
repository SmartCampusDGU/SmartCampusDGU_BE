package org.smartcampus.smartcampus_be.domain.outlier.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierSettings;

@Getter
@Builder
public class OutlierSettingsResponse {

    private Long id;
    private Integer monitoringDurationMinutes;
    private Integer duplicatePreventionMinutes;
    private Integer dangerNotificationMinutes;
    private Integer cautionNotificationMinutes;

    public static OutlierSettingsResponse from(OutlierSettings settings) {
        return OutlierSettingsResponse.builder()
                .id(settings.getId())
                .monitoringDurationMinutes(settings.getMonitoringDurationMinutes())
                .duplicatePreventionMinutes(settings.getDuplicatePreventionMinutes())
                .dangerNotificationMinutes(settings.getDangerNotificationMinutes())
                .cautionNotificationMinutes(settings.getCautionNotificationMinutes())
                .build();
    }
}