package org.smartcampus.smartcampus_be.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    // 어떤 종류의 에러인지 구분하기 위한 에러 타입 필드
    private final ErrorType errorType;

    public CustomException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public int getHttpStatus() {
        return errorType.getHttpStatusCode();
    }
}
