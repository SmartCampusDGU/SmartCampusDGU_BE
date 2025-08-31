package org.smartcampus.smartcampus_be.domain.sensor.repository;

import org.smartcampus.smartcampus_be.domain.sensor.entity.DataType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataTypeRepository extends JpaRepository<DataType, Long> {
}
