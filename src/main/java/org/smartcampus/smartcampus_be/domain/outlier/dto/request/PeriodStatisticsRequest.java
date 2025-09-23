package org.smartcampus.smartcampus_be.domain.outlier.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PeriodStatisticsRequest {

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}