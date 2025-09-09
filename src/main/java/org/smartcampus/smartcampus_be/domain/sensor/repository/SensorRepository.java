package org.smartcampus.smartcampus_be.domain.sensor.repository;

import org.smartcampus.smartcampus_be.domain.room.entity.Room;
import org.smartcampus.smartcampus_be.domain.sensor.entity.Sensor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

    Optional<Sensor> findByMacAddress(String macAddress);

    @EntityGraph(attributePaths = {"room"})
    Page<Sensor> findByRoom(Room room, Pageable pageable);

    Optional<Sensor> findByMacAddressAndRoom(String macAddress, Room room);
}
