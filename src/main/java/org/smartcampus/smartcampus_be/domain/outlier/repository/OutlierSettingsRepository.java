package org.smartcampus.smartcampus_be.domain.outlier.repository;

import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OutlierSettingsRepository extends JpaRepository<OutlierSettings, Long> {

    @Query("SELECT os FROM OutlierSettings os ORDER BY os.id ASC LIMIT 1")
    Optional<OutlierSettings> findFirst();
}