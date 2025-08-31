package org.smartcampus.smartcampus_be.domain.room.dto.response;

import lombok.Builder;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomType;
import org.smartcampus.smartcampus_be.global.common.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PagingRoomTypeResponse(
        PageResponse page,
        List<RoomTypeResponse> roomTypes
) {
    public static PagingRoomTypeResponse of(Page<RoomType> pageRoomType, List<RoomTypeResponse> roomTypes) {
        return PagingRoomTypeResponse.builder()
                .page(PageResponse.from(pageRoomType))
                .roomTypes(roomTypes)
                .build();
    }
}
