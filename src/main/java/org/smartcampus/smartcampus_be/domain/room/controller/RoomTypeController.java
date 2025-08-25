package org.smartcampus.smartcampus_be.domain.room.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.room.dto.*;
import org.smartcampus.smartcampus_be.domain.room.service.RoomTypeService;
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
@RequestMapping("/api/rooms/types")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> createRoomType(
            @RequestBody @Valid CreateRoomTypeRequestDto request
    ) {
        Long roomTypeId = roomTypeService.createRoomType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(SuccessType.ROOM_TYPE_CREATE_SUCCESS, Map.of("id", roomTypeId))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomTypeListItemDto>>> getRoomTypes() {
        List<RoomTypeListItemDto> roomTypes = roomTypeService.getRoomTypes();
        return ResponseEntity.ok(ApiResponse.success(SuccessType.ROOM_TYPE_LIST_SUCCESS, roomTypes));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{roomTypeId}")
    public ResponseEntity<ApiResponse<String>> deleteRoomType(@PathVariable Long roomTypeId) {
        roomTypeService.deleteRoomType(roomTypeId);
        return ResponseEntity.ok((ApiResponse<String>) ApiResponse.success(SuccessType.ROOM_TYPE_DELETE_SUCCESS));
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{roomTypeId}")
    public ResponseEntity<ApiResponse<RoomTypeDetailDto>> updateRoomType(
            @PathVariable Long roomTypeId,
            @RequestBody @Valid UpdateRoomTypeRequestDto request
    ) {
        RoomTypeDetailDto updatedRoomType = roomTypeService.updateRoomType(roomTypeId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessType.ROOM_TYPE_UPDATE_SUCCESS, updatedRoomType));
    }
}