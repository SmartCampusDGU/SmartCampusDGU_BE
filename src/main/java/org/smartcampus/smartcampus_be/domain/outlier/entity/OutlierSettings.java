package org.smartcampus.smartcampus_be.domain.outlier.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.smartcampus.smartcampus_be.global.common.domain.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "outlier_settings")
public class OutlierSettings extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "monitoring_duration_minutes", nullable = false)
    private Integer monitoringDurationMinutes;

    @Column(name = "duplicate_prevention_minutes", nullable = false)
    private Integer duplicatePreventionMinutes;

    @Column(name = "danger_notification_minutes", nullable = false)
    private Integer dangerNotificationMinutes;

    @Column(name = "caution_notification_minutes", nullable = false)
    private Integer cautionNotificationMinutes;

    @Builder
    public OutlierSettings(Integer monitoringDurationMinutes, Integer duplicatePreventionMinutes,
                          Integer dangerNotificationMinutes, Integer cautionNotificationMinutes) {
        this.monitoringDurationMinutes = monitoringDurationMinutes;
        this.duplicatePreventionMinutes = duplicatePreventionMinutes;
        this.dangerNotificationMinutes = dangerNotificationMinutes;
        this.cautionNotificationMinutes = cautionNotificationMinutes;
    }

    public void updateSettings(Integer monitoringDurationMinutes, Integer duplicatePreventionMinutes,
                              Integer dangerNotificationMinutes, Integer cautionNotificationMinutes) {
        if (monitoringDurationMinutes != null) {
            this.monitoringDurationMinutes = monitoringDurationMinutes;
        }
        if (duplicatePreventionMinutes != null) {
            this.duplicatePreventionMinutes = duplicatePreventionMinutes;
        }
        if (dangerNotificationMinutes != null) {
            this.dangerNotificationMinutes = dangerNotificationMinutes;
        }
        if (cautionNotificationMinutes != null) {
            this.cautionNotificationMinutes = cautionNotificationMinutes;
        }
    }

    public static OutlierSettings createDefault() {
        return OutlierSettings.builder()
                .monitoringDurationMinutes(180)
                .duplicatePreventionMinutes(180)
                .dangerNotificationMinutes(60)
                .cautionNotificationMinutes(120)
                .build();
    }
}