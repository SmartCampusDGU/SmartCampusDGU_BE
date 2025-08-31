package org.smartcampus.smartcampus_be.domain.room.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.room.dto.request.CreateRoomTypeRequest;
import org.smartcampus.smartcampus_be.domain.room.dto.request.UpdateRoomTypeRequest;
import org.smartcampus.smartcampus_be.domain.room.dto.response.PagingRoomTypeResponse;
import org.smartcampus.smartcampus_be.domain.room.dto.response.RoomTypeResponse;
import org.smartcampus.smartcampus_be.domain.room.service.RoomTypeService;
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
@RequestMapping("/api/rooms/types")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> createRoomType(
            @RequestBody @Valid CreateRoomTypeRequest request
    ) {
        Long roomTypeId = roomTypeService.createRoomType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(SuccessType.ROOM_TYPE_CREATE_SUCCESS, Map.of("id", roomTypeId))
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<ApiResponse<PagingRoomTypeResponse>> getRoomTypes(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        PagingRoomTypeResponse roomTypes = roomTypeService.getRoomTypes(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(SuccessType.ROOM_TYPE_LIST_SUCCESS, roomTypes));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{roomTypeId}")
    public ResponseEntity<ApiResponse<String>> deleteRoomType(@PathVariable Long roomTypeId) {
        roomTypeService.deleteRoomType(roomTypeId);
        return ResponseEntity.ok((ApiResponse<String>) ApiResponse.success(SuccessType.ROOM_TYPE_DELETE_SUCCESS));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{roomTypeId}")
    public ResponseEntity<ApiResponse<RoomTypeResponse>> updateRoomType(
            @PathVariable Long roomTypeId,
            @RequestBody @Valid UpdateRoomTypeRequest request
    ) {
        RoomTypeResponse updatedRoomType = roomTypeService.updateRoomType(roomTypeId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessType.ROOM_TYPE_UPDATE_SUCCESS, updatedRoomType));
    }
}