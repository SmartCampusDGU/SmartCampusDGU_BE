package org.smartcampus.smartcampus_be.domain.outlier.repository;

import org.smartcampus.smartcampus_be.domain.member.entity.Member;
import org.smartcampus.smartcampus_be.domain.outlier.entity.CheckStatus;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierLevel;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OutlierLogRepository extends JpaRepository<OutlierLog, Long> {
    
    @Query("SELECT o FROM OutlierLog o " +
           "JOIN FETCH o.sensorData sd " +
           "JOIN FETCH sd.sensor s " +
           "JOIN FETCH s.room r " +
           "JOIN FETCH sd.dataType dt " +
           "WHERE o.member = :member " +
           "ORDER BY o.createdAt DESC")
    Page<OutlierLog> findByMemberOrderByCreatedAtDesc(@Param("member") Member member, Pageable pageable);
    
    @Query("SELECT o FROM OutlierLog o " +
           "JOIN FETCH o.sensorData sd " +
           "JOIN FETCH sd.sensor s " +
           "JOIN FETCH s.room r " +
           "JOIN FETCH sd.dataType dt " +
           "WHERE o.member = :member " +
           "AND o.level = :level " +
           "ORDER BY o.createdAt DESC")
    Page<OutlierLog> findByMemberAndLevelOrderByCreatedAtDesc(@Param("member") Member member, 
                                                               @Param("level") OutlierLevel level, 
                                                               Pageable pageable);
    
    @Query("SELECT o FROM OutlierLog o " +
           "JOIN FETCH o.sensorData sd " +
           "JOIN FETCH sd.sensor s " +
           "JOIN FETCH s.room r " +
           "JOIN FETCH sd.dataType dt " +
           "WHERE o.member = :member " +
           "AND o.checkStatus = :checkStatus " +
           "ORDER BY o.createdAt DESC")
    Page<OutlierLog> findByMemberAndCheckStatusOrderByCreatedAtDesc(@Param("member") Member member,
                                                                     @Param("checkStatus") CheckStatus checkStatus,
                                                                     Pageable pageable);
    
    @Query("SELECT o FROM OutlierLog o " +
           "JOIN FETCH o.sensorData sd " +
           "JOIN FETCH sd.sensor s " +
           "JOIN FETCH s.room r " +
           "JOIN FETCH sd.dataType dt " +
           "WHERE o.member = :member " +
           "AND o.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY o.createdAt DESC")
    Page<OutlierLog> findByMemberAndCreatedAtBetweenOrderByCreatedAtDesc(@Param("member") Member member,
                                                                          @Param("startDate") LocalDateTime startDate,
                                                                          @Param("endDate") LocalDateTime endDate,
                                                                          Pageable pageable);
    
    @Query("SELECT o FROM OutlierLog o " +
           "JOIN FETCH o.sensorData sd " +
           "JOIN FETCH sd.sensor s " +
           "JOIN FETCH s.room r " +
           "JOIN FETCH sd.dataType dt " +
           "WHERE o.member = :member " +
           "AND s.room.id = :roomId " +
           "ORDER BY o.createdAt DESC")
    Page<OutlierLog> findByMemberAndRoomIdOrderByCreatedAtDesc(@Param("member") Member member,
                                                               @Param("roomId") Long roomId,
                                                               Pageable pageable);
    
    @Query("SELECT COUNT(o) FROM OutlierLog o WHERE o.member = :member AND o.checkStatus = :checkStatus")
    Long countByMemberAndCheckStatus(@Param("member") Member member, @Param("checkStatus") CheckStatus checkStatus);
    
    @Query("SELECT COUNT(o) FROM OutlierLog o WHERE o.member = :member AND o.level = :level")
    Long countByMemberAndLevel(@Param("member") Member member, @Param("level") OutlierLevel level);
    
    @Query("SELECT o FROM OutlierLog o " +
           "LEFT JOIN FETCH o.member " +
           "JOIN FETCH o.sensorData sd " +
           "JOIN FETCH sd.sensor s " +
           "JOIN FETCH s.room r " +
           "JOIN FETCH sd.dataType dt " +
           "ORDER BY o.createdAt DESC")
    Page<OutlierLog> findAllOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("SELECT COUNT(o) FROM OutlierLog o WHERE o.checkStatus = :checkStatus")
    Long countByCheckStatus(@Param("checkStatus") CheckStatus checkStatus);
    
    @Query("SELECT COUNT(o) FROM OutlierLog o WHERE o.level = :level")
    Long countByLevel(@Param("level") OutlierLevel level);
    
    @Query("SELECT o FROM OutlierLog o " +
           "LEFT JOIN FETCH o.member " +
           "JOIN FETCH o.sensorData sd " +
           "JOIN FETCH sd.sensor s " +
           "JOIN FETCH s.room r " +
           "JOIN FETCH sd.dataType dt " +
           "WHERE (:level IS NULL OR o.level = :level) " +
           "AND (:checkStatus IS NULL OR o.checkStatus = :checkStatus) " +
           "AND (:roomId IS NULL OR s.room.id = :roomId) " +
           "AND (:startDate IS NULL OR o.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR o.createdAt <= :endDate) " +
           "ORDER BY o.createdAt DESC")
    Page<OutlierLog> findWithSearchConditions(@Param("level") OutlierLevel level,
                                              @Param("checkStatus") CheckStatus checkStatus,
                                              @Param("roomId") Long roomId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate,
                                              Pageable pageable);
    
    @Query("SELECT o FROM OutlierLog o " +
           "WHERE o.sensorData.sensor.id = :sensorId " +
           "AND o.sensorData.dataType.id = :dataTypeId " +
           "AND o.level = :level " +
           "AND o.createdAt >= :timeThreshold " +
           "ORDER BY o.createdAt DESC")
    List<OutlierLog> findRecentOutlierBySensorAndDataTypeAndLevel(@Param("sensorId") Long sensorId,
                                                                   @Param("dataTypeId") Long dataTypeId,
                                                                   @Param("level") OutlierLevel level,
                                                                   @Param("timeThreshold") LocalDateTime timeThreshold);

    @Query("SELECT o FROM OutlierLog o " +
           "WHERE o.actionStatus = 'COMPLETED' " +
           "AND o.completedAt IS NOT NULL")
    List<OutlierLog> findCompletedOutliersForMonitoring();

    @Query("SELECT o FROM OutlierLog o " +
           "WHERE o.sensorData.sensor.id = :sensorId " +
           "AND o.sensorData.dataType.id = :dataTypeId " +
           "AND o.actionStatus = 'COMPLETED' " +
           "AND o.completedAt IS NOT NULL")
    List<OutlierLog> findMonitoringOutliersBySensorAndDataType(@Param("sensorId") Long sensorId,
                                                               @Param("dataTypeId") Long dataTypeId);

    @Query("SELECT COUNT(o) FROM OutlierLog o " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate")
    Long countOutliersByPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT dt.name as dataTypeName, dt.unit as unit, COUNT(o) as count " +
           "FROM OutlierLog o " +
           "JOIN o.sensorData sd " +
           "JOIN sd.dataType dt " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY dt.id, dt.name, dt.unit " +
           "ORDER BY COUNT(o) DESC")
    List<Object[]> findOutlierTypeStatisticsByPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT s.mac_address as macAddress, r.room_number as roomNumber, " +
           "COUNT(o.id) as outlierCount, " +
           "AVG(CASE WHEN o.completed_at IS NOT NULL " +
           "    THEN TIMESTAMPDIFF(MINUTE, o.created_at, o.completed_at) " +
           "    ELSE NULL END) as averageDurationMinutes " +
           "FROM outlier_log o " +
           "JOIN sensor_data sd ON o.sensor_data_id = sd.id " +
           "JOIN sensor s ON sd.sensor_id = s.id " +
           "JOIN room r ON s.room_id = r.id " +
           "WHERE o.created_at BETWEEN :startDate AND :endDate " +
           "GROUP BY s.id, s.mac_address, r.room_number " +
           "ORDER BY COUNT(o.id) DESC", nativeQuery = true)
    List<Object[]> findSensorOutlierSummaryByPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o " +
           "FROM OutlierLog o " +
           "JOIN FETCH o.sensorData sd " +
           "JOIN FETCH sd.sensor s " +
           "JOIN FETCH s.room r " +
           "JOIN FETCH sd.dataType dt " +
           "LEFT JOIN FETCH o.member m " +
           "WHERE o.completedAt IS NOT NULL " +
           "AND o.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY o.completedAt DESC")
    List<OutlierLog> findActionRecordsByPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}