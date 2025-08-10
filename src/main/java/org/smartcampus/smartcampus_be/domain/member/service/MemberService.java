package org.smartcampus.smartcampus_be.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.member.dto.LoginRequestDto;
import org.smartcampus.smartcampus_be.domain.member.dto.LoginResponseDto;
import org.smartcampus.smartcampus_be.domain.member.dto.MemberCreateRequestDto;
import org.smartcampus.smartcampus_be.domain.member.dto.MemberCreateResponseDto;
import org.smartcampus.smartcampus_be.domain.member.entity.Member;
import org.smartcampus.smartcampus_be.domain.member.entity.Role;
import org.smartcampus.smartcampus_be.domain.member.repository.MemberRepository;
import org.smartcampus.smartcampus_be.global.common.jwt.JwtTokenProvider;
import org.smartcampus.smartcampus_be.global.common.jwt.PrincipalHandler;
import org.smartcampus.smartcampus_be.global.common.jwt.UserAuthentication;
import org.smartcampus.smartcampus_be.global.exception.CustomException;
import org.smartcampus.smartcampus_be.global.exception.ErrorType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final PrincipalHandler principalHandler;

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

    @Transactional
    public MemberCreateResponseDto createMember(MemberCreateRequestDto request) {

        // db에 요청하는 사람이 있는지
        Member requester = memberRepository.findById(principalHandler.getUserIdFromPrincipal())
                               .orElseThrow(() -> new CustomException(ErrorType.JWT_UNAUTHORIZED_EXCEPTION));

        if (memberRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new CustomException(ErrorType.DUPLICATE_USERNAME);
        }

        Member member = Member.builder()
                            .username(request.getUsername())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .name(request.getName())
                            .description(request.getDescription())
                            .role(Role.ADMIN) //등록시 관리자 권한 주도록 고정
                            .build();

        return new MemberCreateResponseDto(memberRepository.save(member).getId());
    }
}
