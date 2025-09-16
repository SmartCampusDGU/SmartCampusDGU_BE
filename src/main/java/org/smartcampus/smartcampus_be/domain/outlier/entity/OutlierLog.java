package org.smartcampus.smartcampus_be.domain.outlier.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.smartcampus.smartcampus_be.domain.member.entity.Member;
import org.smartcampus.smartcampus_be.domain.sensor.entity.SensorData;
import org.smartcampus.smartcampus_be.global.common.domain.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "outlier_log")
public class OutlierLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_data_id", nullable = false)
    private SensorData sensorData;

    @Column(nullable = false)
    private Double value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutlierLevel level;

    @Enumerated(EnumType.STRING)
    private ActionStatus actionStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckStatus checkStatus;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    
    @Builder
    public OutlierLog(Member member, SensorData sensorData, Double value, OutlierLevel level, ActionStatus actionStatus, CheckStatus checkStatus) {
        this.member = member;
        this.sensorData = sensorData;
        this.value = value;
        this.level = level;
        this.actionStatus = actionStatus;
        this.checkStatus = checkStatus;
    }

    public void updateCheckStatus(CheckStatus checkStatus) {
        this.checkStatus = checkStatus;
    }

    public void updateActionStatus(ActionStatus actionStatus) {
        this.actionStatus = actionStatus;
    }

    public void markAsCompleted() {
        this.actionStatus = ActionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void assignMember(Member member) {
        this.member = member;
    }

    public boolean isMonitoringExpired(int monitoringDurationMinutes) {
        return completedAt != null && LocalDateTime.now().isAfter(completedAt.plusMinutes(monitoringDurationMinutes));
    }
}
