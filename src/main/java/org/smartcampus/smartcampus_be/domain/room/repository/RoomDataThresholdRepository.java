package org.smartcampus.smartcampus_be.domain.room.repository;

import org.smartcampus.smartcampus_be.domain.room.entity.RoomDataThreshold;
import org.smartcampus.smartcampus_be.domain.sensor.entity.DataType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomDataThresholdRepository extends JpaRepository<RoomDataThreshold, Long> {
    Optional<RoomDataThreshold> findByRoomIdAndDataType(Long roomId, DataType dataType);
}
