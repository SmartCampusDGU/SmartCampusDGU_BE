package org.smartcampus.smartcampus_be.domain.member.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.member.dto.LoginRequestDto;
import org.smartcampus.smartcampus_be.domain.member.dto.LoginResponseDto;
import org.smartcampus.smartcampus_be.domain.member.service.MemberService;
import org.smartcampus.smartcampus_be.global.response.ApiResponse;
import org.smartcampus.smartcampus_be.global.response.SuccessType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@RequestBody @Valid LoginRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success(SuccessType.LOGIN_SUCCESS, memberService.login(request)));
    }
}

