package org.smartcampus.smartcampus_be.domain.sensor.repository;

import org.smartcampus.smartcampus_be.domain.sensor.entity.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
}