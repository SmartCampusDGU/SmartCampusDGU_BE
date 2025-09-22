package org.smartcampus.smartcampus_be.domain.outlier.service;

import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierSettings;
import org.smartcampus.smartcampus_be.domain.outlier.repository.OutlierSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutlierSettingsService {

    private final OutlierSettingsRepository outlierSettingsRepository;

    public OutlierSettings getSettings() {
        return outlierSettingsRepository.findFirst()
                .orElseGet(() -> {
                    OutlierSettings defaultSettings = OutlierSettings.createDefault();
                    return outlierSettingsRepository.save(defaultSettings);
                });
    }

    @Transactional
    public OutlierSettings updateSettings(Integer monitoringDurationMinutes, Integer duplicatePreventionMinutes,
                                         Integer dangerNotificationMinutes, Integer cautionNotificationMinutes) {
        OutlierSettings settings = getSettings();
        settings.updateSettings(monitoringDurationMinutes, duplicatePreventionMinutes,
                               dangerNotificationMinutes, cautionNotificationMinutes);
        return settings;
    }

    public Integer getMonitoringDurationMinutes() {
        return getSettings().getMonitoringDurationMinutes();
    }

    public Integer getDuplicatePreventionMinutes() {
        return getSettings().getDuplicatePreventionMinutes();
    }

    public Integer getDangerNotificationMinutes() {
        return getSettings().getDangerNotificationMinutes();
    }

    public Integer getCautionNotificationMinutes() {
        return getSettings().getCautionNotificationMinutes();
    }
}