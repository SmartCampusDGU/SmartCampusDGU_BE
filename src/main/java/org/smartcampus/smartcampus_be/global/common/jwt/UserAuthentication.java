package org.smartcampus.smartcampus_be.global.common.jwt;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 사용자 인증 정보를 담는 커스텀 인증 객체
 */
public class UserAuthentication extends UsernamePasswordAuthenticationToken {

    public UserAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    public static UserAuthentication createUserAuthentication(Long userId) {
        return new UserAuthentication(userId, null, null);
    }
}
