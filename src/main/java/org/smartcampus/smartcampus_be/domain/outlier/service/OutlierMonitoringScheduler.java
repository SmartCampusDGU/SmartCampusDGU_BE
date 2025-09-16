package org.smartcampus.smartcampus_be.domain.outlier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutlierMonitoringScheduler {

    private final OutlierService outlierService;

    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    public void cleanupExpiredMonitoringOutliers() {
        log.info("Starting cleanup of expired monitoring outliers");

        try {
            outlierService.cleanupExpiredMonitoringOutliers();
            log.info("Successfully completed cleanup of expired monitoring outliers");
        } catch (Exception e) {
            log.error("Error occurred while cleaning up expired monitoring outliers", e);
        }
    }
}