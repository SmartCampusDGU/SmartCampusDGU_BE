package org.smartcampus.smartcampus_be.global.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.smartcampus.smartcampus_be.global.exception.ErrorType;
import org.smartcampus.smartcampus_be.global.response.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * 사용자가 인증은 되었지만 특정 자원에 접근할 권한이 없는 경우
 * Spring Security가 이 핸들러를 호출하여 적절한 JSON 응답을 클라이언트에게 반환
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden 상태 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<?> apiResponse = ApiResponse.error(ErrorType.ACCESS_DENIED);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}

