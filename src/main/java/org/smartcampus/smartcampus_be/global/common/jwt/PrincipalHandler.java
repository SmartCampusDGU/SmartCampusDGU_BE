package org.smartcampus.smartcampus_be.global.common.jwt;

import org.smartcampus.smartcampus_be.global.exception.CustomException;
import org.smartcampus.smartcampus_be.global.exception.ErrorType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 현재 인증된 사용자의 Principal에서 사용자 ID 추출
 */
@Component
public class PrincipalHandler {

    // 인증되지 않은 사용자의 기본 principal 값
    private static final String ANONYMOUS_USER = "anonymousUser";

    // SecurityContext에서 Principal을 꺼내 사용자 ID로 반환
    // 인증되지 않은 사용자는 예외 발생
    public Long getUserIdFromPrincipal() {
        Authentication authentication = getAuthenticationOrThrow();

        Object principal = authentication.getPrincipal();
        isPrincipalNull(principal);

        if (principal instanceof Long l) return l;
        if (principal instanceof Integer i) return i.longValue();
        if (principal instanceof String s) {
            try { return Long.valueOf(s); }
            catch (NumberFormatException e) {
                throw new CustomException(ErrorType.JWT_UNAUTHORIZED_EXCEPTION);
            }
        }

        throw new CustomException(ErrorType.JWT_UNAUTHORIZED_EXCEPTION);
    }

    // principal이 anonymousUser인 경우 인증되지 않은 사용자로 간주하고 예외 발생
    public void isPrincipalNull(final Object principal) {
        if (principal == null || ANONYMOUS_USER.equals(principal.toString())) {
            throw new CustomException(ErrorType.JWT_UNAUTHORIZED_EXCEPTION);
        }
    }

    // Authentication null/미인증 방어
    private Authentication getAuthenticationOrThrow() {
        var context = SecurityContextHolder.getContext();
        if (context == null || context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
            throw new CustomException(ErrorType.JWT_UNAUTHORIZED_EXCEPTION);
        }
        return context.getAuthentication();
    }
}
