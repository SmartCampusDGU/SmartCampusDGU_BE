package org.smartcampus.smartcampus_be.domain.room.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.room.dto.request.CreateRoomRequest;
import org.smartcampus.smartcampus_be.domain.room.dto.request.UpdateRoomRequest;
import org.smartcampus.smartcampus_be.domain.room.dto.response.PagingRoomResponse;
import org.smartcampus.smartcampus_be.domain.room.dto.response.RoomResponse;
import org.smartcampus.smartcampus_be.domain.room.service.RoomService;
import org.smartcampus.smartcampus_be.global.response.ApiResponse;
import org.smartcampus.smartcampus_be.global.response.SuccessType;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RoomController {

    private final RoomService roomService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<Map<String, Long>>> createRoom(@RequestBody @Valid CreateRoomRequest request) {
        Long roomId = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(SuccessType.ROOM_CREATE_SUCCESS, Map.of("id", roomId))
        );
    }

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<PagingRoomResponse>> getRooms(
            @RequestParam(value = "roomType", required = false) Long roomTypeId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        PagingRoomResponse rooms = roomService.getRooms(roomTypeId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(SuccessType.ROOM_LIST_SUCCESS, rooms));
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomDetail(@PathVariable Long roomId) {
        RoomResponse roomResponse = roomService.getRoomDetail(roomId);
        return ResponseEntity.ok(ApiResponse.success(SuccessType.ROOM_DETAIL_SUCCESS, roomResponse));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable Long roomId,
            @RequestBody @Valid UpdateRoomRequest request
    ) {
        RoomResponse updatedRoom = roomService.updateRoom(roomId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessType.ROOM_UPDATE_SUCCESS, updatedRoom));
    }

}
