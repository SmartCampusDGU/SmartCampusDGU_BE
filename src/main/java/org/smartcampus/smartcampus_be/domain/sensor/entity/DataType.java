package org.smartcampus.smartcampus_be.domain.sensor.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomDataThreshold;
import org.smartcampus.smartcampus_be.domain.room.entity.RoomTypeDataThreshold;
import org.smartcampus.smartcampus_be.global.common.domain.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "data_type")
public class DataType extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 10, nullable = false)
    private String unit;

    @Builder
    public DataType(String name, String unit) {
        this.name = name;
        this.unit = unit;
    }

    @OneToMany(mappedBy = "dataType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SensorData> sensorDatas = new ArrayList<>();

    @OneToMany(mappedBy = "dataType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SensorDataType> sensorDataTypes = new ArrayList<>();

    @OneToMany(mappedBy = "dataType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomDataThreshold> roomDataThresholds = new ArrayList<>();

    @OneToMany(mappedBy = "dataType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomTypeDataThreshold> roomTypeDataThresholds = new ArrayList<>();
}
