package org.smartcampus.smartcampus_be.domain.room.service;

import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.room.dto.request.RoomTypeDataTypeRequest;
import org.smartcampus.smartcampus_be.domain.room.dto.request.CreateRoomTypeRequest;
import org.smartcampus.smartcampus_be.domain.room.dto.request.UpdateRoomTypeRequest;
import org.smartcampus.smartcampus_be.domain.room.dto.response.PagingRoomTypeResponse;
import org.smartcampus.smartcampus_be.domain.room.dto.response.RoomTypeResponse;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomType;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomTypeDataThreshold;
import org.smartcampus.smartcampus_be.domain.room.repository.RoomRepository;
import org.smartcampus.smartcampus_be.domain.room.repository.RoomTypeDataThresholdRepository;
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
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomTypeDataThresholdRepository roomTypeDataThresholdRepository;
    private final DataTypeRepository dataTypeRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public Long createRoomType(CreateRoomTypeRequest request) {
        RoomType roomType = request.toEntity();
        RoomType savedRoomType = roomTypeRepository.save(roomType);
        List<Long> dataTypeIds = request.dataTypes().stream()
                .map(RoomTypeDataTypeRequest::id)
                .toList();
        List<DataType> dataTypes = dataTypeRepository.findAllById(dataTypeIds);
        if (dataTypes.size() != dataTypeIds.size()) {
            throw new CustomException(ErrorType.DATA_TYPE_NOT_FOUND);
        }
        Map<Long, DataType> dataTypeMap = dataTypes.stream()
                .collect(Collectors.toMap(DataType::getId, dataType -> dataType));
        List<RoomTypeDataThreshold> thresholds = request.dataTypes().stream()
                .map(roomTypeDataTypeRequest -> {
                    DataType dataType = dataTypeMap.get(roomTypeDataTypeRequest.id());
                    return roomTypeDataTypeRequest.toEntity(savedRoomType, dataType);
                })
                .toList();
        roomTypeDataThresholdRepository.saveAll(thresholds);
        return savedRoomType.getId();
    }

    public PagingRoomTypeResponse getRoomTypes(Pageable pageable) {
        Page<Long> idPage = roomTypeRepository.findPageIds(pageable);
        List<Long> ids = idPage.getContent();
        List<RoomType> fetched = roomTypeRepository.findByIdIn(ids);
        Map<Long, Integer> order = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) order.put(ids.get(i), i);
        List<RoomType> ordered = fetched.stream()
                .collect(Collectors.toMap(
                        RoomType::getId,
                        Function.identity(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ))
                .values().stream()
                .sorted(Comparator.comparingInt(rt -> order.get(rt.getId())))
                .toList();
        Page<RoomType> pageRoomTypes = new PageImpl<>(ordered, pageable, idPage.getTotalElements());
        List<RoomTypeResponse> roomTypeResponses = pageRoomTypes.getContent().stream()
                .map(RoomTypeResponse::from)
                .collect(Collectors.toList());
        return PagingRoomTypeResponse.of(pageRoomTypes, roomTypeResponses);
    }

    @Transactional
    public void deleteRoomType(Long roomTypeId) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new CustomException(ErrorType.ROOM_TYPE_NOT_FOUND));
        if (roomRepository.existsByRoomType(roomType)) {
            throw new CustomException(ErrorType.ROOM_TYPE_IN_USE);
        }
        roomTypeRepository.delete(roomType);
    }

    @Transactional
    public RoomTypeResponse updateRoomType(Long roomTypeId, UpdateRoomTypeRequest request) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new CustomException(ErrorType.ROOM_TYPE_NOT_FOUND));
        if (request.name() != null) {
            roomType.updateName(request.name());
        }
        if (request.description() != null) {
            roomType.updateDescription(request.description());
        }
        roomType.getRoomTypeDataThresholds().clear();
        if (request.dataTypes() != null && !request.dataTypes().isEmpty()) {
            List<Long> dataTypeIds = request.dataTypes().stream()
                    .map(RoomTypeDataTypeRequest::id)
                    .toList();
            List<DataType> dataTypes = dataTypeRepository.findAllById(dataTypeIds);
            if (dataTypes.size() != dataTypeIds.size()) {
                throw new CustomException(ErrorType.DATA_TYPE_NOT_FOUND);
            }
            Map<Long, DataType> dataTypeMap = dataTypes.stream()
                    .collect(Collectors.toMap(DataType::getId, dataType -> dataType));
            List<RoomTypeDataThreshold> thresholds = request.dataTypes().stream()
                    .map(roomTypeDataTypeRequest -> {
                        DataType dataType = dataTypeMap.get(roomTypeDataTypeRequest.id());
                        return roomTypeDataTypeRequest.toEntity(roomType, dataType);
                    })
                    .toList();
            roomTypeDataThresholdRepository.saveAll(thresholds);
        }
        return RoomTypeResponse.from(roomType);
    }
}