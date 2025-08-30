package org.smartcampus.smartcampus_be.domain.room.entity;

import jakarta.persistence.*;
import lombok.*;
import org.smartcampus.smartcampus_be.domain.sensor.entity.DataType;
import org.smartcampus.smartcampus_be.global.common.domain.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "room_type_data_threshold")
public class RoomTypeDataThreshold extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_type_id", nullable = false)
    private DataType dataType;

    @Column(nullable = false)
    private Double cautionMin;

    @Column(nullable = false)
    private Double cautionMax;

    @Column(nullable = false)
    private Double dangerMin;

    @Column(nullable = false)
    private Double dangerMax;

    @Column(nullable = false)
    private Double emergencyMin;

    @Column(nullable = false)
    private Double emergencyMax;
}
