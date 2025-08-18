package org.smartcampus.smartcampus_be.controller;

import jakarta.validation.Valid;
import org.smartcampus.smartcampus_be.dto.ApiResponse;
import org.smartcampus.smartcampus_be.dto.CreateRoomTypeRequestDto;
import org.smartcampus.smartcampus_be.dto.RoomTypeListItemDto;
import org.smartcampus.smartcampus_be.service.RoomTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.smartcampus.smartcampus_be.dto.RoomTypeDetailDto;
import org.smartcampus.smartcampus_be.dto.UpdateRoomTypeRequestDto;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms/types")
@Validated
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    // ------------------------------------
    // 1) [POST] 공간유형 등록 (인증 필요)
    // ------------------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> createRoomType(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody CreateRoomTypeRequestDto req
    ) {
        // 컨벤션: 없으면 400, 형식오류면 401
        if (authorization == null || authorization.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "토큰이 없습니다."));
        }
        if (!authorization.startsWith("Bearer ") || authorization.substring(7).isBlank()) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "토큰이 유효하지 않습니다."));
        }

        try {
            Long id = roomTypeService.createRoomType(req);
            return ResponseEntity.ok(
                    ApiResponse.ok("공간 유형 등록에 성공했습니다.", Map.of("id", id))
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(500, "서버 내부 오류"));
        }
    }

    // ------------------------------------
    // 2) [GET] 공간유형 목록 조회 (인증 불필요)
    //    응답: [{ roomType, sensorTypes[] }]
    // ------------------------------------
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomTypeListItemDto>>> getRoomTypes() {
        try {
            List<RoomTypeListItemDto> items = roomTypeService.getRoomTypes();
            return ResponseEntity.ok(
                    ApiResponse.ok("공간유형 목록 조회 성공", items)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(500, "서버 내부 오류"));
        }
    }

    // RoomTypeController.java

    @DeleteMapping("/{roomTypeId}")
    public ResponseEntity<ApiResponse<Void>> deleteRoomType(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long roomTypeId
    ) {
        // 1) 토큰 체크 (우리 컨벤션)
        if (authorization == null || authorization.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "토큰이 없습니다."));
        }
        if (!authorization.startsWith("Bearer ") || authorization.substring(7).isBlank()) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "토큰이 유효하지 않습니다."));
        }

        try {
            roomTypeService.deleteRoomType(roomTypeId);
            return ResponseEntity.ok(
                    ApiResponse.ok("공간유형이 성공적으로 삭제되었습니다.", null)
            );
        } catch (IllegalArgumentException e) {
            // 서비스에서 '해당 공간유형 없음'일 때
            return ResponseEntity.status(404)
                    .body(ApiResponse.error(404, "잘못된 경로입니다."));
            // 필요하면 메시지를 "해당 공간유형을 찾을 수 없습니다."로 바꿔도 OK
        } catch (IllegalStateException e) {
            // 서비스에서 '사용중(호실 존재)'일 때
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "해당 유형에 속한 호실이 존재하여 삭제할 수 없습니다."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(500, "서버 내부 오류"));
        }
    }

    @PatchMapping("/{roomTypeId}")
    public ResponseEntity<ApiResponse<RoomTypeDetailDto>> updateRoomType(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long roomTypeId,
            @Valid @RequestBody UpdateRoomTypeRequestDto req
    ) {
        // 토큰 규칙
        if (authorization == null || authorization.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "토큰이 없습니다."));
        }
        if (!authorization.startsWith("Bearer ") || authorization.substring(7).isBlank()) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "토큰이 유효하지 않습니다."));
        }

        try {
            RoomTypeDetailDto dto = roomTypeService.updateRoomType(roomTypeId, req);
            return ResponseEntity.ok(
                    ApiResponse.ok("공간유형별 임계값이 성공적으로 수정되었습니다.", dto)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(404, e.getMessage())); // 존재 X 등
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(500, "서버 내부 오류"));
        }
    }


}
