package org.smartcampus.smartcampus_be.domain.room.repository;

import org.smartcampus.smartcampus_be.domain.room.entity.RoomTypeDataThreshold;
import org.smartcampus.smartcampus_be.domain.sensor.entity.DataType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomTypeDataThresholdRepository extends JpaRepository<RoomTypeDataThreshold, Long> {
    Optional<RoomTypeDataThreshold> findByRoomTypeIdAndDataType(Long roomTypeId, DataType dataType);
}
