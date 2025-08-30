package org.smartcampus.smartcampus_be.domain.outlier.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActionStatus {
    NONE("미조치"),
    PLANNED("조치예정"),
    IN_PROGRESS("조치중"),
    COMPLETED("조치완료");

    private final String description;
}
