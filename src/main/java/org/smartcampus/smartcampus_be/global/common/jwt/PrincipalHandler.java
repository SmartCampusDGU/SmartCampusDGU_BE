package org.smartcampus.smartcampus_be.global.common.jwt;

import org.smartcampus.smartcampus_be.global.exception.CustomException;
import org.smartcampus.smartcampus_be.global.exception.ErrorType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 현재 인증된 사용자의 Principal에서 사용자 ID 추출
 */
@Component
public class PrincipalHandler {

    // 서 인증되지 않은 사용자의 기본 principal 값
    private static final String ANONYMOUS_USER = "anonymousUser";

    // SecurityContext에서 Principal을 꺼내 사용자 ID로 반환
    // 인증되지 않은 사용자는 예외 발생
    public Long getUserIdFromPrincipal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        isPrincipalNull(principal);
        return Long.valueOf(principal.toString());
    }

    // principal이 anonymousUser인 경우 인증되지 않은 사용자로 간주하고 예외 발생
    public void isPrincipalNull(
        final Object principal
    ) {
        if (principal.toString().equals(ANONYMOUS_USER)) {
            throw new CustomException(ErrorType.JWT_UNAUTHORIZED_EXCEPTION);
        }
    }
}
