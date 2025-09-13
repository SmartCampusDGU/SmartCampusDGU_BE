package org.smartcampus.smartcampus_be.domain.sensor.repository;

import org.smartcampus.smartcampus_be.domain.room.entity.Room;
import org.smartcampus.smartcampus_be.domain.sensor.entity.Sensor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

    @Query("SELECT s FROM Sensor s JOIN FETCH s.room r JOIN FETCH r.roomType WHERE s.macAddress = :macAddress")
    Optional<Sensor> findByMacAddress(@Param("macAddress") String macAddress);

    @EntityGraph(attributePaths = {"room"})
    Page<Sensor> findByRoom(Room room, Pageable pageable);

    Optional<Sensor> findByMacAddressAndRoom(String macAddress, Room room);
}
