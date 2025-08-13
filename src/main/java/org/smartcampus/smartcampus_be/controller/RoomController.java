package org.smartcampus.smartcampus_be.controller;

import jakarta.validation.Valid;
import org.smartcampus.smartcampus_be.dto.ApiResponse;
import org.smartcampus.smartcampus_be.dto.CreateRoomRequestDto;
import org.smartcampus.smartcampus_be.dto.UpdateRoomRequestDto;
import org.smartcampus.smartcampus_be.dto.RoomDetailDto;
import org.smartcampus.smartcampus_be.dto.RoomListItemDto;
import org.smartcampus.smartcampus_be.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }


    // 1) 방 생성 (POST /api/rooms)

    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<Map<String, Long>>> create(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @Valid @RequestBody CreateRoomRequestDto req
    ) {
        // 400: 헤더 없음
        if (auth == null || auth.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "토큰이 없습니다."));
        }
        // 401: 형식 오류
        if (!auth.startsWith("Bearer ") || auth.substring(7).isBlank()) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "토큰이 유효하지 않습니다."));
        }

        Long id = roomService.createRoom(req);
        return ResponseEntity.ok(ApiResponse.ok("공간 생성에 성공했습니다.", Map.of("id", id)));
    }


    // 2) 공간 목록 조회 (GET /api/rooms?roomType=...)

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<RoomListItemDto>>> getRooms(
            @RequestParam(value = "roomType", required = false) String roomType
    ) {
        List<RoomListItemDto> list = roomService.getRooms(roomType);
        return ResponseEntity.ok(ApiResponse.ok("공간 목록 조회에 성공했습니다.", list));
    }


    // 3) 공간 상세 조회 (GET /api/rooms/{roomId})

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ApiResponse<RoomDetailDto>> getRoomDetail(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @PathVariable Long roomId
    ) {
        // 400: 헤더 없음
        if (auth == null || auth.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "토큰이 없습니다."));
        }
        // 401: 형식 오류
        if (!auth.startsWith("Bearer ") || auth.substring(7).isBlank()) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error(401, "토큰이 유효하지 않습니다."));
        }

        try {
            RoomDetailDto dto = roomService.getRoomDetail(roomId);
            return ResponseEntity.ok(ApiResponse.ok("공간 상세 조회에 성공했습니다.", dto));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error(404, "잘못된 경로입니다."));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error(500, "서버 내부 오류"));
        }
    }

    // 4) 공간 수정 (PATCH /rooms/{roomId})
    @PatchMapping("/rooms/{roomId}")
    public ResponseEntity<ApiResponse<RoomDetailDto>> updateRoom(
            @PathVariable Long roomId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody UpdateRoomRequestDto req
    ) {
        // 헤더 규칙(사진 명세)
        if (authorization == null || authorization.isBlank()) {
            return ResponseEntity.badRequest() .body(ApiResponse.error(400, "토큰이 없습니다."));
        }
        if (!authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "토큰이 유효하지 않습니다."));
        }

        RoomDetailDto data = roomService.updateRoom(roomId, req);
        return ResponseEntity.ok(ApiResponse.ok("공간 수정에 성공했습니다.", data));
    }

}
