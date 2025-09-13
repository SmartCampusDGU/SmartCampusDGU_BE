package org.smartcampus.smartcampus_be.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smartcampus.smartcampus_be.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.smartcampus.smartcampus_be.global.exception.ErrorType.INTERNAL_SERVER_ERROR;
import static org.smartcampus.smartcampus_be.global.exception.ErrorType.REQUEST_VALIDATION_EXCEPTION;

@Slf4j
@RestControllerAdvice
@Component
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    /**
     * 400 VALIDATION_ERROR -> 유효성 검증 실패 시 발생하는 예외 처리
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ApiResponse<?> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {

        Errors errors = e.getBindingResult();
        Map<String, String> validateDetails = new HashMap<>();

        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validateDetails.put(validKeyName, error.getDefaultMessage());
        }
        return ApiResponse.error(REQUEST_VALIDATION_EXCEPTION, validateDetails);
    }

    /**
     * CUSTOM_ERROR -> 사용자 정의 예외(CustomException) 처리, 에러 타입과 상태코드 커스텀해서 반환
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ApiResponse<?>> handleBusinessException(CustomException e) {

        log.error("[EXCEPTION] CustomException Occured: {}", e.getMessage(), e);

        return ResponseEntity.status(e.getHttpStatus())
                   .body(ApiResponse.error(e.getErrorType(), e.getMessage()));
    }

    /**
     * 500 INTERNAL_SERVER -> 처리되지 않은 예외 전반 처리
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    protected ApiResponse<?> handleException(final Exception e, final HttpServletRequest request) throws IOException {
        log.error("[EXCEPTION] Unhandled Exception Occurred: {}", e.getMessage(), e);
        return ApiResponse.error(INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
