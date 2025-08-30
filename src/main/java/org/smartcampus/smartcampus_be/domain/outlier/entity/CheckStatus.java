package org.smartcampus.smartcampus_be.domain.outlier.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CheckStatus {
    CONFIRMED("확인"),
    UNCONFIRMED("미확인");

    private final String description;
}
