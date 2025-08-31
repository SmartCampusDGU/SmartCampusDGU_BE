package org.smartcampus.smartcampus_be.domain.room.service;

import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.room.dto.request.CreateRoomRequest;
import org.smartcampus.smartcampus_be.domain.room.dto.request.RoomDataTypeRequest;
import org.smartcampus.smartcampus_be.domain.room.dto.request.UpdateRoomRequest;
import org.smartcampus.smartcampus_be.domain.room.dto.response.PagingRoomResponse;
import org.smartcampus.smartcampus_be.domain.room.dto.response.RoomResponse;
import org.smartcampus.smartcampus_be.domain.room.entity.Room;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomDataThreshold;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomType;
import org.smartcampus.smartcampus_be.domain.room.repository.RoomDataThresholdRepository;
import org.smartcampus.smartcampus_be.domain.room.repository.RoomRepository;
import org.smartcampus.smartcampus_be.domain.room.repository.RoomTypeRepository;
import org.smartcampus.smartcampus_be.domain.sensor.entity.DataType;
import org.smartcampus.smartcampus_be.domain.sensor.repository.DataTypeRepository;
import org.smartcampus.smartcampus_be.global.exception.CustomException;
import org.smartcampus.smartcampus_be.global.exception.ErrorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final DataTypeRepository dataTypeRepository;
    private final RoomDataThresholdRepository roomDataThresholdRepository;

    @Transactional
    public Long createRoom(CreateRoomRequest request) {
        RoomType roomType = roomTypeRepository.findById(request.roomTypeId())
                .orElseThrow(() -> new CustomException(ErrorType.ROOM_TYPE_NOT_FOUND));
        Room room = request.toEntity(roomType);
        Room savedRoom = roomRepository.save(room);
        List<Long> dataTypeIds = request.dataTypes().stream()
                .map(RoomDataTypeRequest::id)
                .toList();
        List<DataType> dataTypes = dataTypeRepository.findAllById(dataTypeIds);
        if (dataTypes.size() != dataTypeIds.size()) {
            throw new CustomException(ErrorType.DATA_TYPE_NOT_FOUND);
        }
        Map<Long, DataType> dataTypeMap = dataTypes.stream()
                .collect(Collectors.toMap(DataType::getId, dataType -> dataType));
        List<RoomDataThreshold> thresholds = request.dataTypes().stream()
                .map(roomDataTypeRequest -> {
                    DataType dataType = dataTypeMap.get(roomDataTypeRequest.id());
                    return roomDataTypeRequest.toEntity(savedRoom, dataType);
                })
                .toList();
        roomDataThresholdRepository.saveAll(thresholds);
        return savedRoom.getId();
    }

    public PagingRoomResponse getRooms(Long roomTypeId, Pageable pageable) {
        Page<Long> idPage;
        if (roomTypeId != null) {
            RoomType roomType = roomTypeRepository.findById(roomTypeId)
                    .orElseThrow(() -> new CustomException(ErrorType.ROOM_TYPE_NOT_FOUND));
            idPage = roomRepository.findPageIdsByRoomType(roomType, pageable);
        } else {
            idPage = roomRepository.findPageIds(pageable);
        }
        List<Long> ids = idPage.getContent();
        if (ids.isEmpty()) {
            Page<Room> emptyPage = new PageImpl<>(List.of(), pageable, idPage.getTotalElements());
            return PagingRoomResponse.from(emptyPage, List.of());
        }
        List<Room> fetched = roomRepository.findByIdIn(ids);
        Map<Long, Integer> order = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) order.put(ids.get(i), i);
        List<Room> ordered = fetched.stream()
                .collect(Collectors.toMap(
                        Room::getId,
                        Function.identity(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ))
                .values().stream()
                .sorted(Comparator.comparingInt(r -> order.get(r.getId())))
                .toList();
        List<RoomResponse> roomResponses = ordered.stream()
                .map(RoomResponse::from)
                .collect(Collectors.toList());
        Page<Room> roomPage = new PageImpl<>(ordered, pageable, idPage.getTotalElements());
        return PagingRoomResponse.from(roomPage, roomResponses);
    }

    public RoomResponse getRoomDetail(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorType.ROOM_NOT_FOUND));
        return RoomResponse.from(room);
    }

    @Transactional
    public RoomResponse updateRoom(Long roomId, UpdateRoomRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorType.ROOM_NOT_FOUND));
        room.getRoomDataThresholds().clear();
        if (request.dataTypes() != null && !request.dataTypes().isEmpty()) {
            List<Long> dataTypeIds = request.dataTypes().stream()
                    .map(RoomDataTypeRequest::id)
                    .toList();
            List<DataType> dataTypes = dataTypeRepository.findAllById(dataTypeIds);
            if (dataTypes.size() != dataTypeIds.size()) {
                throw new CustomException(ErrorType.DATA_TYPE_NOT_FOUND);
            }
            Map<Long, DataType> dataTypeMap = dataTypes.stream()
                    .collect(Collectors.toMap(DataType::getId, dataType -> dataType));
            List<RoomDataThreshold> thresholds = request.dataTypes().stream()
                    .map(roomDataTypeRequest -> {
                        DataType dataType = dataTypeMap.get(roomDataTypeRequest.id());
                        return roomDataTypeRequest.toEntity(room, dataType);
                    })
                    .toList();
            roomDataThresholdRepository.saveAll(thresholds);
        }
        return RoomResponse.from(room);
    }
}
