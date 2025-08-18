package org.smartcampus.smartcampus_be.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "room_type_sensors")
public class RoomTypeSensor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private RoomType roomType;

    @Column(nullable = false)
    private String name;     // 예: 온도, 습도
    private String unit;     // 예: °C, %

    // 임계값(주의/위험/응급)
    private Double cautionMin;
    private Double cautionMax;
    private Double dangerMin;
    private Double dangerMax;
    private Double emergencyMin;
    private Double emergencyMax;

    // getter/setter
    public Long getId() { return id; }
    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Double getCautionMin() { return cautionMin; }
    public void setCautionMin(Double v) { this.cautionMin = v; }
    public Double getCautionMax() { return cautionMax; }
    public void setCautionMax(Double v) { this.cautionMax = v; }
    public Double getDangerMin() { return dangerMin; }
    public void setDangerMin(Double v) { this.dangerMin = v; }
    public Double getDangerMax() { return dangerMax; }
    public void setDangerMax(Double v) { this.dangerMax = v; }
    public Double getEmergencyMin() { return emergencyMin; }
    public void setEmergencyMin(Double v) { this.emergencyMin = v; }
    public Double getEmergencyMax() { return emergencyMax; }
    public void setEmergencyMax(Double v) { this.emergencyMax = v; }
}
