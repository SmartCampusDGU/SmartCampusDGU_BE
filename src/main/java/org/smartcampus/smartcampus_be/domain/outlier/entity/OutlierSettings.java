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

    @Builder
    public OutlierSettings(Integer monitoringDurationMinutes, Integer duplicatePreventionMinutes) {
        this.monitoringDurationMinutes = monitoringDurationMinutes;
        this.duplicatePreventionMinutes = duplicatePreventionMinutes;
    }

    public void updateSettings(Integer monitoringDurationMinutes, Integer duplicatePreventionMinutes) {
        if (monitoringDurationMinutes != null) {
            this.monitoringDurationMinutes = monitoringDurationMinutes;
        }
        if (duplicatePreventionMinutes != null) {
            this.duplicatePreventionMinutes = duplicatePreventionMinutes;
        }
    }

    public static OutlierSettings createDefault() {
        return OutlierSettings.builder()
                .monitoringDurationMinutes(180)
                .duplicatePreventionMinutes(180)
                .build();
    }
}