package org.smartcampus.smartcampus_be.domain;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "room_sensors")
public class RoomSensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- 연관관계 ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // --- 기본 정보 ---
    @Column(nullable = false, length = 50)
    private String name;

    // thresholds를 직접 넣지 않으면 true
    @Column(nullable = false)
    private Boolean useDefault = true;

    // 단위(없으면 빈 문자열로 사용)
    @Column(length = 20)
    private String unit;

    // --- 임계값: Double(널 허용) ---
    // precision/scale은 필요 시만 지정
    @Column(name = "caution_min")
    private Double cautionMin;
    @Column(name = "caution_max")
    private Double cautionMax;

    @Column(name = "danger_min")
    private Double dangerMin;
    @Column(name = "danger_max")
    private Double dangerMax;

    @Column(name = "emergency_min")
    private Double emergencyMin;
    @Column(name = "emergency_max")
    private Double emergencyMax;

    // --- getters/setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }    // ← 반드시 void

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Boolean getUseDefault() { return useDefault; }
    public boolean isUseDefault() { return Boolean.TRUE.equals(useDefault); } // 편의
    public void setUseDefault(Boolean useDefault) { this.useDefault = useDefault; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getCautionMin() { return cautionMin; }
    public void setCautionMin(Double cautionMin) { this.cautionMin = cautionMin; }
    public Double getCautionMax() { return cautionMax; }
    public void setCautionMax(Double cautionMax) { this.cautionMax = cautionMax; }

    public Double getDangerMin() { return dangerMin; }
    public void setDangerMin(Double dangerMin) { this.dangerMin = dangerMin; }
    public Double getDangerMax() { return dangerMax; }
    public void setDangerMax(Double dangerMax) { this.dangerMax = dangerMax; }

    public Double getEmergencyMin() { return emergencyMin; }
    public void setEmergencyMin(Double emergencyMin) { this.emergencyMin = emergencyMin; }
    public Double getEmergencyMax() { return emergencyMax; }
    public void setEmergencyMax(Double emergencyMax) { this.emergencyMax = emergencyMax; }

    // (선택) equals/hashCode by id
}
