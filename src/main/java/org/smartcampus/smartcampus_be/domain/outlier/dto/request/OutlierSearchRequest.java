package org.smartcampus.smartcampus_be.domain.outlier.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.smartcampus.smartcampus_be.domain.outlier.entity.CheckStatus;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierLevel;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class OutlierSearchRequest {
    private OutlierLevel level;
    private CheckStatus checkStatus;
    private Long roomId;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
}