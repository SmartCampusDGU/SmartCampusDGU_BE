package org.smartcampus.smartcampus_be.domain.outlier.service;

import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.member.entity.Member;
import org.smartcampus.smartcampus_be.domain.member.repository.MemberRepository;
import org.smartcampus.smartcampus_be.domain.outlier.dto.request.OutlierSearchRequest;
import org.smartcampus.smartcampus_be.domain.outlier.dto.request.UpdateOutlierStatusRequest;
import org.smartcampus.smartcampus_be.domain.outlier.entity.*;
import org.smartcampus.smartcampus_be.domain.outlier.repository.OutlierLogRepository;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomDataThreshold;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomTypeDataThreshold;
import org.smartcampus.smartcampus_be.domain.room.repository.RoomDataThresholdRepository;
import org.smartcampus.smartcampus_be.domain.room.repository.RoomTypeDataThresholdRepository;
import org.smartcampus.smartcampus_be.domain.sensor.entity.DataType;
import org.smartcampus.smartcampus_be.domain.sensor.entity.SensorData;
import org.smartcampus.smartcampus_be.global.exception.CustomException;
import org.smartcampus.smartcampus_be.global.exception.ErrorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutlierService {

    private final OutlierLogRepository outlierLogRepository;
    private final RoomDataThresholdRepository roomDataThresholdRepository;
    private final RoomTypeDataThresholdRepository roomTypeDataThresholdRepository;
    private final MemberRepository memberRepository;
    private final OutlierSettingsService outlierSettingsService;
    
    @Transactional
    public void detectAndSaveOutlier(SensorData sensorData) {
        OutlierLevel level = detectOutlierLevel(sensorData);

        if (level != OutlierLevel.SAFE) {
            // 모니터링 중인 완료된 이상치 로그 처리
            handleMonitoringOutliers(sensorData, level);

            // 중복 방지: 최근 N분 내에 동일한 센서+데이터타입+레벨의 이상치가 있는지 확인
            if (isDuplicateOutlier(sensorData, level)) {
                return; // 중복이면 저장하지 않음
            }

            OutlierLog outlierLog = OutlierLog.builder()
                    .member(null)
                    .sensorData(sensorData)
                    .value(Double.parseDouble(sensorData.getValue()))
                    .level(level)
                    .checkStatus(CheckStatus.UNCONFIRMED)
                    .actionStatus(ActionStatus.NONE)
                    .build();

            outlierLogRepository.save(outlierLog);
        } else {
            // 데이터가 양호할 때 모니터링 중인 완료된 이상치 로그 삭제
            deleteMonitoringOutliersIfSafe(sensorData);
        }
    }
    
    public OutlierLevel detectOutlierLevel(SensorData sensorData) {
        Long roomId = sensorData.getSensor().getRoom().getId();
        DataType dataType = sensorData.getDataType();
        double value = Double.parseDouble(sensorData.getValue());
        
        Optional<RoomDataThreshold> roomThreshold = roomDataThresholdRepository
                .findByRoomIdAndDataType(roomId, dataType);
        
        if (roomThreshold.isPresent() && roomThreshold.get().getIsModified()) {
            return determineLevel(value, roomThreshold.get());
        } else {
            Long roomTypeId = sensorData.getSensor().getRoom().getRoomType().getId();
            Optional<RoomTypeDataThreshold> roomTypeThreshold = roomTypeDataThresholdRepository
                    .findByRoomTypeIdAndDataType(roomTypeId, dataType);
            
            if (roomTypeThreshold.isPresent()) {
                return determineLevel(value, roomTypeThreshold.get());
            }
        }
        
        return OutlierLevel.SAFE;
    }
    
    private OutlierLevel determineLevel(double value, RoomDataThreshold threshold) {
        if (value < threshold.getEmergencyMin() || value > threshold.getEmergencyMax()) {
            return OutlierLevel.EMERGENCY;
        }
        if (value < threshold.getDangerMin() || value > threshold.getDangerMax()) {
            return OutlierLevel.DANGER;
        }
        if (value < threshold.getCautionMin() || value > threshold.getCautionMax()) {
            return OutlierLevel.CAUTION;
        }
        return OutlierLevel.SAFE;
    }
    
    private OutlierLevel determineLevel(double value, RoomTypeDataThreshold threshold) {
        if (value < threshold.getEmergencyMin() || value > threshold.getEmergencyMax()) {
            return OutlierLevel.EMERGENCY;
        }
        if (value < threshold.getDangerMin() || value > threshold.getDangerMax()) {
            return OutlierLevel.DANGER;
        }
        if (value < threshold.getCautionMin() || value > threshold.getCautionMax()) {
            return OutlierLevel.CAUTION;
        }
        return OutlierLevel.SAFE;
    }
    
    private boolean isDuplicateOutlier(SensorData sensorData, OutlierLevel level) {
        Long sensorId = sensorData.getSensor().getId();
        Long dataTypeId = sensorData.getDataType().getId();

        Integer preventionMinutes = getPreventionMinutesByLevel(level);
        LocalDateTime timeThreshold = sensorData.getCreatedAt().minusMinutes(preventionMinutes);

        List<OutlierLog> recentOutliers = outlierLogRepository
                .findRecentOutlierBySensorAndDataTypeAndLevel(sensorId, dataTypeId, level, timeThreshold);

        return !recentOutliers.isEmpty();
    }

    private Integer getPreventionMinutesByLevel(OutlierLevel level) {
        return switch (level) {
            case DANGER -> outlierSettingsService.getDangerNotificationMinutes();
            case CAUTION -> outlierSettingsService.getCautionNotificationMinutes();
            default -> outlierSettingsService.getDuplicatePreventionMinutes();
        };
    }
    
    public OutlierLog getOutlierLogById(Long outlierLogId, Long memberId) {
        OutlierLog outlierLog = outlierLogRepository.findById(outlierLogId)
                .orElseThrow(() -> new IllegalArgumentException("OutlierLog not found with id: " + outlierLogId));

        return outlierLog;
    }
    
    @Transactional
    public OutlierLog updateOutlierStatus(Long outlierLogId, Long memberId, UpdateOutlierStatusRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorType.MEMBER_NOT_FOUND));
        OutlierLog outlierLog = getOutlierLogById(outlierLogId, memberId);

        // 상태 업데이트 시 조치 담당자 할당
        if (outlierLog.getMember() == null) {
            outlierLog.assignMember(member);
        }

        if (request.getCheckStatus() != null) {
            outlierLog.updateCheckStatus(request.getCheckStatus());
        }

        if (request.getActionStatus() != null) {
            if (request.getActionStatus() == ActionStatus.COMPLETED) {
                // COMPLETED 상태로 변경 시 모니터링 설정
                outlierLog.markAsCompleted();
            } else {
                outlierLog.updateActionStatus(request.getActionStatus());
            }
        }

        return outlierLog;
    }
    
    public Page<OutlierLog> getAllOutlierLogs(Pageable pageable) {
        return outlierLogRepository.findAllOrderByCreatedAtDesc(pageable);
    }
    
    public Page<OutlierLog> getAllOutlierLogs(OutlierSearchRequest searchRequest, Pageable pageable) {
        return outlierLogRepository.findWithSearchConditions(
                searchRequest.getLevel(),
                searchRequest.getCheckStatus(),
                searchRequest.getRoomId(),
                searchRequest.getStartDate(),
                searchRequest.getEndDate(),
                pageable
        );
    }
    
    public Long getAllUnconfirmedCount() {
        return outlierLogRepository.countByCheckStatus(CheckStatus.UNCONFIRMED);
    }
    
    public Long getAllOutlierCountByLevel(OutlierLevel level) {
        return outlierLogRepository.countByLevel(level);
    }

    private void handleMonitoringOutliers(SensorData sensorData, OutlierLevel level) {
        Long sensorId = sensorData.getSensor().getId();
        Long dataTypeId = sensorData.getDataType().getId();

        List<OutlierLog> monitoringOutliers = outlierLogRepository
                .findMonitoringOutliersBySensorAndDataType(sensorId, dataTypeId)
                .stream()
                .filter(outlier -> !outlier.isMonitoringExpired(outlierSettingsService.getMonitoringDurationMinutes()))
                .toList();

        for (OutlierLog monitoringOutlier : monitoringOutliers) {
            if (monitoringOutlier.isMonitoringExpired(outlierSettingsService.getMonitoringDurationMinutes())) {
                // 모니터링 기간이 만료되었으므로 새로운 이상치로 간주
                outlierLogRepository.delete(monitoringOutlier);

                // 새로운 이상치 로그 생성 (기존 담당자 정보 유지)
                OutlierLog newOutlierLog = OutlierLog.builder()
                        .member(monitoringOutlier.getMember())
                        .sensorData(sensorData)
                        .value(Double.parseDouble(sensorData.getValue()))
                        .level(level)
                        .checkStatus(CheckStatus.UNCONFIRMED)
                        .actionStatus(ActionStatus.NONE)
                        .build();

                outlierLogRepository.save(newOutlierLog);
            }
        }
    }

    private void deleteMonitoringOutliersIfSafe(SensorData sensorData) {
        Long sensorId = sensorData.getSensor().getId();
        Long dataTypeId = sensorData.getDataType().getId();

        List<OutlierLog> monitoringOutliers = outlierLogRepository
                .findMonitoringOutliersBySensorAndDataType(sensorId, dataTypeId)
                .stream()
                .filter(outlier -> !outlier.isMonitoringExpired(outlierSettingsService.getMonitoringDurationMinutes()))
                .toList();

        if (!monitoringOutliers.isEmpty()) {
            outlierLogRepository.deleteAll(monitoringOutliers);
        }
    }

    @Transactional
    public void cleanupExpiredMonitoringOutliers() {
        List<OutlierLog> expiredOutliers = outlierLogRepository
                .findCompletedOutliersForMonitoring()
                .stream()
                .filter(outlier -> outlier.isMonitoringExpired(outlierSettingsService.getMonitoringDurationMinutes()))
                .toList();

        if (!expiredOutliers.isEmpty()) {
            outlierLogRepository.deleteAll(expiredOutliers);
        }
    }
}