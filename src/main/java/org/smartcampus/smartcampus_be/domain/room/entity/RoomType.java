package org.smartcampus.smartcampus_be.domain.room.entity;

import jakarta.persistence.*;
import lombok.*;
import org.smartcampus.smartcampus_be.global.common.domain.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room_types")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RoomType extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RoomTypeSensor> sensors = new ArrayList<>();

    public void addSensor(RoomTypeSensor sensor) {
        sensors.add(sensor);
        sensor.setRoomType(this);
    }
}
