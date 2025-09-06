package org.smartcampus.smartcampus_be.domain.outlier.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierLog;
import org.smartcampus.smartcampus_be.global.common.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PagingOutlierLogResponse {
    PageResponse page;
    private List<OutlierLogResponse> outlierLogs;

    public static PagingOutlierLogResponse from(Page<OutlierLog> page) {
        List<OutlierLogResponse> outlierLogs = page.getContent()
                .stream()
                .map(OutlierLogResponse::from)
                .toList();

        return PagingOutlierLogResponse.builder()
                .page(PageResponse.from(page))
                .outlierLogs(outlierLogs)
                .build();
    }
}