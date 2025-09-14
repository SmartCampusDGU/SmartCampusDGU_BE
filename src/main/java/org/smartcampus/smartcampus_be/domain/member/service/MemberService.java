package org.smartcampus.smartcampus_be.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.member.dto.*;
import org.smartcampus.smartcampus_be.domain.member.entity.Member;
import org.smartcampus.smartcampus_be.domain.member.entity.Role;
import org.smartcampus.smartcampus_be.domain.member.repository.MemberRepository;
import org.smartcampus.smartcampus_be.global.common.jwt.JwtTokenProvider;
import org.smartcampus.smartcampus_be.global.common.jwt.PrincipalHandler;
import org.smartcampus.smartcampus_be.global.common.jwt.UserAuthentication;
import org.smartcampus.smartcampus_be.global.exception.CustomException;
import org.smartcampus.smartcampus_be.global.exception.ErrorType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional
    public void updateMember(Long id, MemberUpdateRequestDto request) {

        Member requester = memberRepository.findById(principalHandler.getUserIdFromPrincipal())
                .orElseThrow(() -> new CustomException(ErrorType.JWT_UNAUTHORIZED_EXCEPTION));

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorType.MEMBER_NOT_FOUND));


        member.update(
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                request.getDescription()
        );
    }

    @Transactional
    public void deleteMembers(MemberDeleteRequestDto request) {

        Member requester = memberRepository.findById(principalHandler.getUserIdFromPrincipal())
                .orElseThrow(() -> new CustomException(ErrorType.JWT_UNAUTHORIZED_EXCEPTION));

        List<Member> members = memberRepository.findAllById(request.getIds());

        if (members.size() != request.getIds().size()) {
            throw new CustomException(ErrorType.MEMBER_NOT_FOUND); // 일부라도 없으면 실패
        }

        memberRepository.deleteAll(members);
    }

    @Transactional(readOnly = true)
    public List<MemberListResponseDto> getAllMembers() {

        Member requester = memberRepository.findById(principalHandler.getUserIdFromPrincipal())
                .orElseThrow(() -> new CustomException(ErrorType.JWT_UNAUTHORIZED_EXCEPTION));

        return memberRepository.findAll().stream()
                .map(member -> new MemberListResponseDto(
                        member.getId(),
                        member.getUsername(),
                        member.getName(),
                        member.getDescription()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MemberListResponseDto getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorType.MEMBER_NOT_FOUND));
        return new MemberListResponseDto(
                member.getId(),
                member.getUsername(),
                member.getName(),
                member.getDescription()
        );
    }

}
