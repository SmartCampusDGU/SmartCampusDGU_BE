package org.smartcampus.smartcampus_be.domain.room.repository;

import org.smartcampus.smartcampus_be.domain.room.entity.Room;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByRoomType(RoomType roomType);

    @Query("select r.id from Room r order by r.id desc")
    Page<Long> findPageIds(Pageable pageable);

    @Query("select r.id from Room r where r.roomType = :roomType order by r.id desc")
    Page<Long> findPageIdsByRoomType(RoomType roomType, Pageable pageable);

    @EntityGraph(attributePaths = {"roomType", "roomDataThresholds", "roomDataThresholds.dataType"})
    List<Room> findByIdIn(List<Long> ids);

    @EntityGraph(attributePaths = {"roomType", "roomDataThresholds", "roomDataThresholds.dataType"})
    Optional<Room> findById(Long id);
}
