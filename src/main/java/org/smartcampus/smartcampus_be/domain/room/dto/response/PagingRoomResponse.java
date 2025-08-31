package org.smartcampus.smartcampus_be.domain.room.dto.response;

import lombok.Builder;
import org.smartcampus.smartcampus_be.domain.room.entity.Room;
import org.smartcampus.smartcampus_be.global.common.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PagingRoomResponse(
        PageResponse page,
        List<RoomResponse> rooms
) {
    public static PagingRoomResponse from(Page<Room> roomPage, List<RoomResponse> roomResponses) {
        return PagingRoomResponse.builder()
                .page(PageResponse.from(roomPage))
                .rooms(roomResponses)
                .build();
    }
}
