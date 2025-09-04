package org.smartcampus.smartcampus_be.domain.sensor.repository;

import org.smartcampus.smartcampus_be.domain.sensor.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
}
