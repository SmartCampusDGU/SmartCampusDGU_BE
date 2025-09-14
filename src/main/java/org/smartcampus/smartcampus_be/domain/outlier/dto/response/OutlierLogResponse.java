package org.smartcampus.smartcampus_be.domain.outlier.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.smartcampus.smartcampus_be.domain.outlier.entity.ActionStatus;
import org.smartcampus.smartcampus_be.domain.outlier.entity.CheckStatus;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierLevel;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierLog;

import java.time.LocalDateTime;

@Getter
@Builder
public class OutlierLogResponse {
    private Long id;
    private Double value;
    private OutlierLevel level;
    private ActionStatus actionStatus;
    private CheckStatus checkStatus;
    private LocalDateTime createdAt;
    private MemberInfo member;
    
    private SensorInfo sensorInfo;
    private RoomInfo roomInfo;
    private DataTypeInfo dataTypeInfo;

    @Getter
    @Builder
    public static class MemberInfo {
        private Long id;
        private String name;
    }
    
    @Getter
    @Builder
    public static class SensorInfo {
        private Long id;
        private String macAddress;
    }
    
    @Getter
    @Builder
    public static class RoomInfo {
        private Long id;
        private String roomNumber;
        private String roomTypeName;
    }
    
    @Getter
    @Builder
    public static class DataTypeInfo {
        private Long id;
        private String name;
        private String unit;
    }
    
    public static OutlierLogResponse from(OutlierLog outlierLog) {
        return OutlierLogResponse.builder()
                .id(outlierLog.getId())
                .value(outlierLog.getValue())
                .level(outlierLog.getLevel())
                .actionStatus(outlierLog.getActionStatus())
                .checkStatus(outlierLog.getCheckStatus())
                .createdAt(outlierLog.getCreatedAt())
                .member(outlierLog.getMember() != null ? MemberInfo.builder()
                        .id(outlierLog.getMember().getId())
                        .name(outlierLog.getMember().getName())
                        .build() : null)
                .sensorInfo(SensorInfo.builder()
                        .id(outlierLog.getSensorData().getSensor().getId())
                        .macAddress(outlierLog.getSensorData().getSensor().getMacAddress())
                        .build())
                .roomInfo(RoomInfo.builder()
                        .id(outlierLog.getSensorData().getSensor().getRoom().getId())
                        .roomNumber(outlierLog.getSensorData().getSensor().getRoom().getRoomNumber())
                        .roomTypeName(outlierLog.getSensorData().getSensor().getRoom().getRoomType().getName())
                        .build())
                .dataTypeInfo(DataTypeInfo.builder()
                        .id(outlierLog.getSensorData().getDataType().getId())
                        .name(outlierLog.getSensorData().getDataType().getName())
                        .unit(outlierLog.getSensorData().getDataType().getUnit())
                        .build())
                .build();
    }
}