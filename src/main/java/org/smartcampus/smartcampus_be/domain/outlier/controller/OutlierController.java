package org.smartcampus.smartcampus_be.domain.outlier.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.outlier.dto.request.OutlierSearchRequest;
import org.smartcampus.smartcampus_be.domain.outlier.dto.request.UpdateOutlierStatusRequest;
import org.smartcampus.smartcampus_be.domain.outlier.dto.response.OutlierLogResponse;
import org.smartcampus.smartcampus_be.domain.outlier.dto.response.OutlierStatsResponse;
import org.smartcampus.smartcampus_be.domain.outlier.dto.response.PagingOutlierLogResponse;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierLevel;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierLog;
import org.smartcampus.smartcampus_be.domain.outlier.service.OutlierService;
import org.smartcampus.smartcampus_be.global.response.ApiResponse;
import org.smartcampus.smartcampus_be.global.response.SuccessType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "이상치 관리", description = "센서 이상치 감지 및 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/outliers")
public class OutlierController {
    
    private final OutlierService outlierService;
    
    @Operation(summary = "이상치 목록 조회", description = "센서에서 감지된 모든 이상치 목록을 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이상치 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ApiResponse<PagingOutlierLogResponse> getOutlierLogs(
            @Parameter(description = "검색 조건") @ModelAttribute OutlierSearchRequest searchRequest,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<OutlierLog> outlierLogs = outlierService.getAllOutlierLogs(searchRequest, pageable);
        
        PagingOutlierLogResponse response = PagingOutlierLogResponse.from(outlierLogs);
        return ApiResponse.success(SuccessType.PROCESS_SUCCESS, response);
    }
    
//    @Operation(summary = "이상치 통계 조회", description = "전체 이상치의 레벨별, 상태별 통계를 조회합니다.")
//    @ApiResponses(value = {
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이상치 통계 조회 성공"),
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
//    })
//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/stats")
//    public ApiResponse<OutlierStatsResponse> getOutlierStats(
//            @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
//        Long unconfirmedCount = outlierService.getAllUnconfirmedCount();
//        Long cautionCount = outlierService.getAllOutlierCountByLevel(OutlierLevel.CAUTION);
//        Long dangerCount = outlierService.getAllOutlierCountByLevel(OutlierLevel.DANGER);
//        Long emergencyCount = outlierService.getAllOutlierCountByLevel(OutlierLevel.EMERGENCY);
//        Long totalCount = cautionCount + dangerCount + emergencyCount;
//
//        OutlierStatsResponse response = OutlierStatsResponse.of(totalCount, unconfirmedCount,
//                                                                cautionCount, dangerCount, emergencyCount);
//        return ApiResponse.success(SuccessType.PROCESS_SUCCESS, response);
//    }
    
    @Operation(summary = "이상치 상세 조회", description = "특정 이상치의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이상치 상세 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "이상치를 찾을 수 없음")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{outlierLogId}")
    public ApiResponse<OutlierLogResponse> getOutlierLog(
            @Parameter(description = "이상치 로그 ID") @PathVariable Long outlierLogId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {

        OutlierLog outlierLog = outlierService.getOutlierLogById(outlierLogId, memberId);
        OutlierLogResponse response = OutlierLogResponse.from(outlierLog);
        return ApiResponse.success(SuccessType.PROCESS_SUCCESS, response);
    }
    
    @Operation(summary = "이상치 상태 업데이트", description = "이상치의 확인 상태 및 조치 상태를 업데이트합니다. 처음 업데이트하는 관리자가 담당자로 자동 배정됩니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이상치 상태 업데이트 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "이상치를 찾을 수 없음")
    })
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{outlierLogId}/status")
    public ApiResponse<OutlierLogResponse> updateOutlierStatus(
            @Parameter(description = "이상치 로그 ID") @PathVariable Long outlierLogId,
            @Parameter(description = "업데이트할 상태 정보") @RequestBody @Valid UpdateOutlierStatusRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal Long memberId) {

        OutlierLog outlierLog = outlierService.updateOutlierStatus(outlierLogId, memberId, request);
        OutlierLogResponse response = OutlierLogResponse.from(outlierLog);
        return ApiResponse.success(SuccessType.PROCESS_SUCCESS, response);
    }
}