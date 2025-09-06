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
           "WHERE o.sensorData.sensor.id = :sensorId " +
           "AND o.sensorData.dataType.id = :dataTypeId " +
           "AND o.level = :level " +
           "AND o.createdAt >= :timeThreshold " +
           "ORDER BY o.createdAt DESC")
    List<OutlierLog> findRecentOutlierBySensorAndDataTypeAndLevel(@Param("sensorId") Long sensorId,
                                                                   @Param("dataTypeId") Long dataTypeId,
                                                                   @Param("level") OutlierLevel level,
                                                                   @Param("timeThreshold") LocalDateTime timeThreshold);
}