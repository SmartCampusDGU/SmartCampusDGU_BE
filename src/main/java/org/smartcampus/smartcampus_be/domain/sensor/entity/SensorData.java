package org.smartcampus.smartcampus_be.domain.sensor.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierLog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sensor_data")
public class SensorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_type_id", nullable = false)
    private DataType dataType;

    @Column(length = 50, nullable = false)
    private String value;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public SensorData(Sensor sensor, DataType dataType, String value, LocalDateTime createdAt) {
        this.sensor = sensor;
        this.dataType = dataType;
        this.value = value;
        this.createdAt = createdAt;
    }

    @OneToMany(mappedBy = "sensorData", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OutlierLog> outlierLogs = new ArrayList<>();
}
