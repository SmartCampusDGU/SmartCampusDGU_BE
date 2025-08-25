package org.smartcampus.smartcampus_be.domain.room.entity;

import jakarta.persistence.*;
import lombok.*;
import org.smartcampus.smartcampus_be.global.common.domain.BaseTimeEntity;

@Entity
@Table(name = "room_sensors")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RoomSensor extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "use_default", nullable = false)
    @Builder.Default
    private Boolean useDefault = true;

    @Column(length = 20)
    private String unit;

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

    public boolean isUseDefault() {
        return Boolean.TRUE.equals(useDefault);
    }
}
