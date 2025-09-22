package org.smartcampus.smartcampus_be.domain.outlier.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.outlier.dto.request.UpdateOutlierSettingsRequest;
import org.smartcampus.smartcampus_be.domain.outlier.dto.response.OutlierSettingsResponse;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierSettings;
import org.smartcampus.smartcampus_be.domain.outlier.service.OutlierSettingsService;
import org.smartcampus.smartcampus_be.global.response.ApiResponse;
import org.smartcampus.smartcampus_be.global.response.SuccessType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "이상치 설정 관리", description = "이상치 감지 및 모니터링 설정 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/outliers/settings")
public class OutlierSettingsController {

    private final OutlierSettingsService outlierSettingsService;

    @Operation(summary = "이상치 설정 조회", description = "현재 적용 중인 이상치 감지 및 모니터링 설정을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "설정 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ApiResponse<OutlierSettingsResponse> getSettings() {
        OutlierSettings settings = outlierSettingsService.getSettings();
        OutlierSettingsResponse response = OutlierSettingsResponse.from(settings);
        return ApiResponse.success(SuccessType.PROCESS_SUCCESS, response);
    }

    @Operation(summary = "이상치 설정 업데이트", description = "이상치 감지 및 모니터링 설정을 업데이트합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "설정 업데이트 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PreAuthorize("isAuthenticated()")
    @PutMapping
    public ApiResponse<OutlierSettingsResponse> updateSettings(
            @Parameter(description = "업데이트할 설정 정보") @RequestBody @Valid UpdateOutlierSettingsRequest request) {

        OutlierSettings settings = outlierSettingsService.updateSettings(
                request.getMonitoringDurationMinutes(),
                request.getDuplicatePreventionMinutes(),
                request.getDangerNotificationMinutes(),
                request.getCautionNotificationMinutes()
        );

        OutlierSettingsResponse response = OutlierSettingsResponse.from(settings);
        return ApiResponse.success(SuccessType.PROCESS_SUCCESS, response);
    }
}