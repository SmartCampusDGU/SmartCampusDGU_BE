package org.smartcampus.smartcampus_be.global.common;

import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record PageResponse(
        int size,
        long totalElements,
        int currentElements,
        int totalPages,
        int currentPage,
        boolean hasNextPage,
        boolean hasPreviousPage,
        boolean isLast
) {
    public static PageResponse from(Page<?> page) {
        return PageResponse.builder()
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .currentElements(page.getNumberOfElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .hasNextPage(page.hasNext())
                .hasPreviousPage(page.hasPrevious())
                .isLast(page.isLast())
                .build();
    }
}
