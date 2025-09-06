package org.smartcampus.smartcampus_be.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorType {

    /**
     * HTTP 400 (BAD REQUEST)
     */
    REQUEST_VALIDATION_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    INVALID_THRESHOLD_REQUEST(HttpStatus.BAD_REQUEST, "수동 임계값 입력 시 thresholds가 필요합니다."),
    REQUIRED_VALUE_MISSING(HttpStatus.BAD_REQUEST, "필요한 값이 없습니다."),
    SENSOR_REQUIRED(HttpStatus.BAD_REQUEST, "센서 항목이 필요합니다."),
    SENSOR_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "센서 이름이 필요합니다."),
    THRESHOLDS_REQUIRED(HttpStatus.BAD_REQUEST, "임계값(thresholds)이 필요합니다."),
    MISSING_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 없습니다."),

    /**
     * HTTP 401 (UNAUTHORIZED)
     */
    JWT_UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "사용자의 로그인 검증을 실패했습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),

    /**
     * HTTP 403 (FORBIDDEN)
     */
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    /**
     *  HTTP 404 (NOT FOUND)
     */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 계정입니다."),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 공간입니다."),
    ROOM_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 공간유형을 찾을 수 없습니다."),
    DATA_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 데이터 타입입니다."),
    SENSOR_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 센서입니다."),

    /**
     * HTTP 409 (CONFLICT)
     */
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
    DUPLICATE_ROOM_NUMBER(HttpStatus.CONFLICT, "이미 존재하는 강의실 번호입니다."),
    DUPLICATE_ROOM_TYPE(HttpStatus.CONFLICT, "이미 존재하는 공간유형입니다."),
    ROOM_TYPE_IN_USE(HttpStatus.CONFLICT, "해당 유형에 속한 호실이 존재하여 삭제할 수 없습니다."),

    /**
     * HTTP 500 (INTERNAL SERVER ERROR)
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 에러가 발생했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
