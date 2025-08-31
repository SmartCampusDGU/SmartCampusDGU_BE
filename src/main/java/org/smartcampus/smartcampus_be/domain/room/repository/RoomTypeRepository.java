package org.smartcampus.smartcampus_be.domain.room.repository;

import org.smartcampus.smartcampus_be.domain.room.entity.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    @Query("select rt.id from RoomType rt order by rt.id desc")
    Page<Long> findPageIds(Pageable pageable);

    @EntityGraph(attributePaths = {"roomTypeDataThresholds", "roomTypeDataThresholds.dataType"})
    List<RoomType> findByIdIn(List<Long> ids);
}
