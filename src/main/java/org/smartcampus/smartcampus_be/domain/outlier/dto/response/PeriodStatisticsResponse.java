package org.smartcampus.smartcampus_be.domain.outlier.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PeriodStatisticsResponse {

    private final int totalOutlierCount;
    private final List<OutlierTypeStatistics> majorOutlierTypes;
    private final ObservationPeriod observationPeriod;
    private final LocalDateTime reportCreatedAt;
    private final List<SensorOutlierSummary> sensorSummaries;
    private final List<EnvironmentStatistics> environmentStatistics;
    private final List<ActionRecord> actionRecords;

    @Getter
    @Builder
    public static class OutlierTypeStatistics {
        private final String dataTypeName;
        private final String unit;
        private final int count;
        private final double percentage;
    }

    @Getter
    @Builder
    public static class ObservationPeriod {
        private final LocalDateTime startDate;
        private final LocalDateTime endDate;
        private final long totalDurationMinutes;
        private final double averageDurationMinutes;
    }

    @Getter
    @Builder
    public static class SensorOutlierSummary {
        private final String macAddress;
        private final String roomNumber;
        private final int outlierCount;
        private final double averageDurationMinutes;
    }

    @Getter
    @Builder
    public static class EnvironmentStatistics {
        private final String indicator;
        private final String unit;
        private final double average;
        private final double maximum;
        private final double minimum;
    }

    @Getter
    @Builder
    public static class ActionRecord {
        private final LocalDateTime actionDateTime;
        private final String actionStatus;
        private final String memberName;
        private final String roomNumber;
        private final String dataTypeName;
    }
}