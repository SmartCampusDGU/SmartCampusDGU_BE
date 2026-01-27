package org.smartcampus.smartcampus_be.domain.actiontoken.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smartcampus.smartcampus_be.domain.actiontoken.dto.response.ActionResponse;
import org.smartcampus.smartcampus_be.domain.actiontoken.service.ActionTokenService;
import org.smartcampus.smartcampus_be.domain.outlier.entity.ActionStatus;
import org.smartcampus.smartcampus_be.global.response.ApiResponse;
import org.smartcampus.smartcampus_be.global.response.SuccessType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/action")
public class ActionTokenController {

    private final ActionTokenService actionTokenService;

    /**
     * 조치 링크 처리
     * 알림톡 버튼 클릭 시 호출되는 API
     * 토큰 검증 → 조치자 등록 → 상태 업데이트
     */
    @GetMapping("/{token}")
    public ApiResponse<ActionResponse> handleAction(
            @PathVariable String token,
            @RequestParam ActionStatus status
    ) {
        log.info("조치 링크 요청 - token: {}, status: {}", token, status);
        ActionResponse response = actionTokenService.validateAndUseToken(token, status);
        return ApiResponse.success(SuccessType.ACTION_REGISTERED, response);
    }
}
