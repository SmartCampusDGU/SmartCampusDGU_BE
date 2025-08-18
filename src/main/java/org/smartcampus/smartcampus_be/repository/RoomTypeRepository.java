package org.smartcampus.smartcampus_be.repository;

import org.smartcampus.smartcampus_be.domain.RoomType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    boolean existsByName(String name);

    // 조회용 N+1 방지
    @EntityGraph(attributePaths = "sensors")
    List<RoomType> findAll();

    // 상세/수정 시 센서까지 로드
    @EntityGraph(attributePaths = "sensors")
    Optional<RoomType> findById(Long id);

    // (선택) 이름으로 상세 조회가 필요할 때
    Optional<RoomType> findByName(String name);

    // (선택) 조인으로 중복 행이 생길 수 있는 환경을 대비한 distinct 버전
    @EntityGraph(attributePaths = "sensors")
    @Query("select distinct r from RoomType r")
    List<RoomType> findAllDistinct();
}
