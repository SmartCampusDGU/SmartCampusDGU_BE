package org.smartcampus.smartcampus_be.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room_types")
public class RoomType {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // 공간유형명

    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomTypeSensor> sensors = new ArrayList<>();

    // 편의 메서드
    public void addSensor(RoomTypeSensor s) {
        sensors.add(s);
        s.setRoomType(this);
    }

    // getter/setter
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<RoomTypeSensor> getSensors() { return sensors; }
}
