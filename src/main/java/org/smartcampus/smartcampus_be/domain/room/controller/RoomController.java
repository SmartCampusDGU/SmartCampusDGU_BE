package org.smartcampus.smartcampus_be.domain.room.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.room.dto.*;
import org.smartcampus.smartcampus_be.domain.room.service.RoomService;
import org.smartcampus.smartcampus_be.global.response.ApiResponse;
import org.smartcampus.smartcampus_be.global.response.SuccessType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RoomController {

    private final RoomService roomService;


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<Map<String, Long>>> createRoom(@RequestBody @Valid CreateRoomRequestDto request) {
        Long roomId = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(SuccessType.ROOM_CREATE_SUCCESS, Map.of("id", roomId))
        );
    }


    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<RoomListItemDto>>> getRooms(
            @RequestParam(value = "roomType", required = false) String roomType
    ) {
        List<RoomListItemDto> rooms = roomService.getRooms(roomType);
        return ResponseEntity.ok(ApiResponse.success(SuccessType.ROOM_LIST_SUCCESS, rooms));
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ApiResponse<RoomDetailDto>> getRoomDetail(@PathVariable Long roomId) {
        RoomDetailDto roomDetail = roomService.getRoomDetail(roomId);
        return ResponseEntity.ok(ApiResponse.success(SuccessType.ROOM_DETAIL_SUCCESS, roomDetail));
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/rooms/{roomId}")
    public ResponseEntity<ApiResponse<RoomDetailDto>> updateRoom(
            @PathVariable Long roomId,
            @RequestBody @Valid UpdateRoomRequestDto request
    ) {
        RoomDetailDto updatedRoom = roomService.updateRoom(roomId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessType.ROOM_UPDATE_SUCCESS, updatedRoom));
    }

}
