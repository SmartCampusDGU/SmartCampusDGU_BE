package org.smartcampus.smartcampus_be.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.member.dto.LoginRequestDto;
import org.smartcampus.smartcampus_be.domain.member.dto.LoginResponseDto;
import org.smartcampus.smartcampus_be.domain.member.entity.Member;
import org.smartcampus.smartcampus_be.domain.member.repository.MemberRepository;
import org.smartcampus.smartcampus_be.global.common.jwt.JwtTokenProvider;
import org.smartcampus.smartcampus_be.global.common.jwt.UserAuthentication;
import org.smartcampus.smartcampus_be.global.exception.CustomException;
import org.smartcampus.smartcampus_be.global.exception.ErrorType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginRequestDto request) {

        Member member = memberRepository.findByUsername(request.getUsername())
                            .orElseThrow(() -> new CustomException(ErrorType.LOGIN_FAILED));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorType.LOGIN_FAILED);
        }

        UserAuthentication authentication = UserAuthentication.createUserAuthentication(member.getId());
        String accessToken = jwtTokenProvider.issueAccessToken(authentication);

        return new LoginResponseDto(accessToken);
    }
}
