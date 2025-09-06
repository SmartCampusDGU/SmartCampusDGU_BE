package org.smartcampus.smartcampus_be.domain.outlier.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OutlierStatsResponse {
    private Long totalCount;
    private Long unconfirmedCount;
    private Long cautionCount;
    private Long dangerCount;
    private Long emergencyCount;
    
    public static OutlierStatsResponse of(Long totalCount, Long unconfirmedCount, 
                                          Long cautionCount, Long dangerCount, Long emergencyCount) {
        return OutlierStatsResponse.builder()
                .totalCount(totalCount)
                .unconfirmedCount(unconfirmedCount)
                .cautionCount(cautionCount)
                .dangerCount(dangerCount)
                .emergencyCount(emergencyCount)
                .build();
    }
}