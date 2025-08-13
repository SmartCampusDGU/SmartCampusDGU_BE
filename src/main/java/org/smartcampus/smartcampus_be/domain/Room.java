package org.smartcampus.smartcampus_be.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String roomNumber;   // 예: "1103"

    @Column(nullable = false, length = 50)
    private String roomType;     // 예: "강의실", "실험실"

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomSensor> sensors = new ArrayList<>();


    public void addSensor(RoomSensor sensor){
        sensor.setRoom(this);
        this.sensors.add(sensor);
    }

    // getters/setters
    public Long getId() { return id; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public List<RoomSensor> getSensors() { return sensors; }
}
