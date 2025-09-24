package org.smartcampus.smartcampus_be.domain.sensor.repository;

import org.smartcampus.smartcampus_be.domain.sensor.entity.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    @Query(value = "SELECT dt.name as indicator, dt.unit as unit, " +
           "AVG(CAST(sd.value AS DECIMAL)) as average, " +
           "MAX(CAST(sd.value AS DECIMAL)) as maximum, " +
           "MIN(CAST(sd.value AS DECIMAL)) as minimum " +
           "FROM sensor_data sd " +
           "JOIN data_type dt ON sd.data_type_id = dt.id " +
           "WHERE sd.created_at BETWEEN :startDate AND :endDate " +
           "AND sd.value REGEXP '^[0-9]+\\.?[0-9]*$' " +
           "AND dt.name NOT LIKE '%connection%' " +
           "AND dt.name NOT LIKE '%missed%' " +
           "AND dt.name NOT LIKE '%error%' " +
           "AND dt.name NOT LIKE '%status%' " +
           "GROUP BY dt.id, dt.name, dt.unit " +
           "ORDER BY dt.name", nativeQuery = true)
    List<Object[]> findEnvironmentStatisticsByPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}