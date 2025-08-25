package org.smartcampus.smartcampus_be.domain.room.repository;

import org.smartcampus.smartcampus_be.domain.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByRoomNumber(String roomNumber);
    List<Room> findByRoomType(String roomType);
}
