package org.smartcampus.smartcampus_be.domain.sensor.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.smartcampus.smartcampus_be.domain.room.entity.Room;
import org.smartcampus.smartcampus_be.global.common.domain.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sensor")
public class Sensor extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(length = 50, nullable = false)
    private String macAddress;

    @Builder
    public Sensor(Room room, String macAddress) {
        this.room = room;
        this.macAddress = macAddress;
    }

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SensorDataType> sensorDataTypes = new ArrayList<>();

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SensorData> sensorDatas = new ArrayList<>();
}

