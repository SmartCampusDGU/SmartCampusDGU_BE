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
    MEMBER_UPDATE_SUCCESS(HttpStatus.OK, "관리자 계정이 수정되었습니다."),
    MEMBER_DELETE_SUCCESS(HttpStatus.OK, "관리자 계정이 삭제되었습니다."),
    MEMBER_GET_SUCCESS(HttpStatus.OK, "관리자 계정 리스트 조회에 성공했습니다."),

    /**
     * ROOM
     */
    ROOM_CREATE_SUCCESS(HttpStatus.CREATED, "공간 생성에 성공했습니다."),
    ROOM_LIST_SUCCESS(HttpStatus.OK, "공간 목록 조회에 성공했습니다."),
    ROOM_DETAIL_SUCCESS(HttpStatus.OK, "공간 상세 조회에 성공했습니다."),
    ROOM_UPDATE_SUCCESS(HttpStatus.OK, "공간 수정에 성공했습니다."),

    /**
     * ROOM TYPE
     */
    ROOM_TYPE_CREATE_SUCCESS(HttpStatus.CREATED, "공간 유형 등록에 성공했습니다."),
    ROOM_TYPE_LIST_SUCCESS(HttpStatus.OK, "공간유형 목록 조회 성공"),
    ROOM_TYPE_DELETE_SUCCESS(HttpStatus.OK, "공간유형이 성공적으로 삭제되었습니다."),
    ROOM_TYPE_UPDATE_SUCCESS(HttpStatus.OK, "공간유형별 임계값이 성공적으로 수정되었습니다."),

    /**
     * SENSOR TYPE
     */
    SENSOR_CREATE_SUCCESS(HttpStatus.CREATED, "센서 등록에 성공했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}