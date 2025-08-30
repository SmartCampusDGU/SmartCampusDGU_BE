package org.smartcampus.smartcampus_be.domain.outlier.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OutlierLevel {
    SAFE("양호"),
    CAUTION("주의"),
    DANGER("위험"),
    EMERGENCY("응급");

    private final String description;
}
