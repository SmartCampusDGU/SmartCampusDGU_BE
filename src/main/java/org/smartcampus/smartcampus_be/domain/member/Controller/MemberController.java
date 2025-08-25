package org.smartcampus.smartcampus_be.domain.member.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.member.dto.*;
import org.smartcampus.smartcampus_be.domain.member.service.MemberService;
import org.smartcampus.smartcampus_be.global.response.ApiResponse;
import org.smartcampus.smartcampus_be.global.response.SuccessType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@RequestBody @Valid LoginRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success(SuccessType.LOGIN_SUCCESS, memberService.login(request)));
    }

    @PostMapping("/log-out")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        return ResponseEntity.ok((ApiResponse<String>) ApiResponse.success(SuccessType.LOGOUT_SUCCESS));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/members")
    public ResponseEntity<ApiResponse<MemberCreateResponseDto>> createMember(@RequestBody @Valid MemberCreateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(SuccessType.MEMBER_CREATE_SUCCESS, memberService.createMember(request))
        );
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/members/{id}")
    public ResponseEntity<ApiResponse<String>> updateMember(@PathVariable Long id, @RequestBody @Valid MemberUpdateRequestDto request) {
        memberService.updateMember(id, request);
        return ResponseEntity.ok((ApiResponse<String>)ApiResponse.success(SuccessType.MEMBER_UPDATE_SUCCESS));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/members")
    public ResponseEntity<ApiResponse<String>> deleteMembers(@RequestBody @Valid MemberDeleteRequestDto request) {
        memberService.deleteMembers(request);
        return ResponseEntity.ok((ApiResponse<String>)ApiResponse.success(SuccessType.MEMBER_DELETE_SUCCESS));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/members")
    public ResponseEntity<ApiResponse<List<MemberListResponseDto>>> getAllMembers() {
        return ResponseEntity.ok(ApiResponse.success(SuccessType.MEMBER_GET_SUCCESS, memberService.getAllMembers()));
    }
}

