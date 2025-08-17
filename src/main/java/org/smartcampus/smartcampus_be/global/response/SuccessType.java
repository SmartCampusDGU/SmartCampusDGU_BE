package org.smartcampus.smartcampus_be.global.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessType {

    /**
     * HTTP 200 OK
     */
    PROCESS_SUCCESS(HttpStatus.OK, "OK"),
    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃에 성공했습니다."),
    MEMBER_CREATE_SUCCESS(HttpStatus.CREATED, "관리자 계정 등록에 성공했습니다."),
    MEMBER_UPDATE_SUCCESS(HttpStatus.OK, "관리자 계정이 수정되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}