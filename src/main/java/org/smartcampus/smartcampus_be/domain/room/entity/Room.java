package org.smartcampus.smartcampus_be.domain.room.entity;

import jakarta.persistence.*;
import lombok.*;
import org.smartcampus.smartcampus_be.global.common.domain.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Room extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", nullable = false, length = 50)
    private String roomNumber;

    @Column(name = "room_type", nullable = false, length = 50)
    private String roomType;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RoomSensor> sensors = new ArrayList<>();

    public void addSensor(RoomSensor sensor) {
        sensor.setRoom(this);
        this.sensors.add(sensor);
    }
}
