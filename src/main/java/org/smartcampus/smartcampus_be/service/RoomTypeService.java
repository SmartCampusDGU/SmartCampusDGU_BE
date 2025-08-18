package org.smartcampus.smartcampus_be.service;

import jakarta.transaction.Transactional;
import org.smartcampus.smartcampus_be.domain.RoomType;
import org.smartcampus.smartcampus_be.domain.RoomTypeSensor;
import org.smartcampus.smartcampus_be.dto.CreateRoomTypeRequestDto;
import org.smartcampus.smartcampus_be.dto.RoomTypeDetailDto;
import org.smartcampus.smartcampus_be.dto.RoomTypeListItemDto;
import org.smartcampus.smartcampus_be.dto.SensorDto;
import org.smartcampus.smartcampus_be.dto.ThresholdDto;
import org.smartcampus.smartcampus_be.dto.UpdateRoomTypeRequestDto;
import org.smartcampus.smartcampus_be.repository.RoomRepository;
import org.smartcampus.smartcampus_be.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;

    public RoomTypeService(RoomTypeRepository roomTypeRepository,
                           RoomRepository roomRepository) {
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
    }

    /** 공간 유형 등록 */
    @Transactional
    public Long createRoomType(CreateRoomTypeRequestDto req) {
        if (req == null || req.getRoomType() == null || req.getRoomType().isBlank()) {
            throw new IllegalArgumentException("필요한 값이 없습니다.");
        }
        if (roomTypeRepository.existsByName(req.getRoomType())) {
            throw new IllegalArgumentException("이미 존재하는 공간유형입니다.");
        }
        if (req.getSensors() == null || req.getSensors().isEmpty()) {
            throw new IllegalArgumentException("센서 항목이 필요합니다.");
        }

        RoomType type = new RoomType();
        type.setName(req.getRoomType());

        for (SensorDto s : req.getSensors()) {
            if (s.getName() == null || s.getName().isBlank()) {
                throw new IllegalArgumentException("센서 이름이 필요합니다.");
            }

            RoomTypeSensor sensor = new RoomTypeSensor();
            sensor.setName(s.getName());
            sensor.setUnit(s.getUnit()); // unit은 POST에서 없어도 됨(null 허용)

            // useDefault=false 일 때만 thresholds 필수
            if (!s.isUseDefault()) {
                ThresholdDto th = s.getThresholds();
                if (th == null) throw new IllegalArgumentException("임계값(thresholds)이 필요합니다.");

                if (th.getCaution() != null) {
                    sensor.setCautionMin(asDouble(th.getCaution().getMin()));
                    sensor.setCautionMax(asDouble(th.getCaution().getMax()));
                }
                if (th.getDanger() != null) {
                    sensor.setDangerMin(asDouble(th.getDanger().getMin()));
                    sensor.setDangerMax(asDouble(th.getDanger().getMax()));
                }
                if (th.getEmergency() != null) {
                    sensor.setEmergencyMin(asDouble(th.getEmergency().getMin()));
                    sensor.setEmergencyMax(asDouble(th.getEmergency().getMax()));
                }
            }

            // 연관관계 편의 메서드 (type 추가 + 역방향 설정)
            type.addSensor(sensor);
        }

        roomTypeRepository.save(type);
        return type.getId();
    }

    /** 공간 유형 목록 조회 (각 유형의 커스텀 센서 이름 리스트 포함) */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<RoomTypeListItemDto> getRoomTypes() {
        return roomTypeRepository.findAll().stream()
                .map(rt -> new RoomTypeListItemDto(
                        rt.getName(),
                        rt.getSensors().stream()
                                .map(RoomTypeSensor::getName)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    /** 공간 유형 삭제 */
    @Transactional
    public void deleteRoomType(Long id) {
        RoomType rt = roomTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공간유형을 찾을 수 없습니다."));

        // Room이 해당 유형을 사용 중이면 삭제 불가
        boolean inUse = !roomRepository.findByRoomType(rt.getName()).isEmpty();
        // existsByRoomType(String roomType) 를 추가했다면 위 한 줄을 아래로 교체:
        // boolean inUse = roomRepository.existsByRoomType(rt.getName());

        if (inUse) {
            throw new IllegalStateException("해당 유형에 속한 호실이 존재하여 삭제할 수 없습니다.");
        }

        roomTypeRepository.delete(rt);
    }

    /**
     * 공간 유형 기준 수정(PATCH)
     * - 요청에 온 센서 이름으로 기존 센서를 찾아 unit/임계값을 갱신
     * - 없으면 새 센서를 추가(필요 없으면 이 부분 제거 가능)
     * - 갱신 후 현재 상태를 RoomTypeDetailDto로 반환
     */
    @Transactional
    public RoomTypeDetailDto updateRoomType(Long id, UpdateRoomTypeRequestDto req) {
        if (req == null || req.getMeasurements() == null) {
            throw new IllegalArgumentException("필요한 값이 없습니다.");
        }

        RoomType rt = roomTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공간유형을 찾을 수 없습니다."));

        // 1) 들어온 측정 항목들로 갱신
        for (SensorDto m : req.getMeasurements()) {
            if (m.getName() == null || m.getName().isBlank()) {
                throw new IllegalArgumentException("센서 이름이 필요합니다.");
            }

            // 이름으로 기존 센서 찾기
            RoomTypeSensor target = rt.getSensors().stream()
                    .filter(s -> m.getName().equals(s.getName()))
                    .findFirst()
                    .orElse(null);

            // 없으면 새로 추가(옵션)
            if (target == null) {
                target = new RoomTypeSensor();
                target.setName(m.getName());
                rt.addSensor(target);
            }

            // unit 갱신(값이 온 경우에만)
            if (m.getUnit() != null && !m.getUnit().isBlank()) {
                target.setUnit(m.getUnit());
            }

            // thresholds 갱신(값이 온 경우에만)
            if (m.getThresholds() != null) {
                ThresholdDto t = m.getThresholds();
                if (t.getCaution() != null) {
                    target.setCautionMin(asDouble(t.getCaution().getMin()));
                    target.setCautionMax(asDouble(t.getCaution().getMax()));
                }
                if (t.getDanger() != null) {
                    target.setDangerMin(asDouble(t.getDanger().getMin()));
                    target.setDangerMax(asDouble(t.getDanger().getMax()));
                }
                if (t.getEmergency() != null) {
                    target.setEmergencyMin(asDouble(t.getEmergency().getMin()));
                    target.setEmergencyMax(asDouble(t.getEmergency().getMax()));
                }
            }
        }

        // 2) 변경 저장
        roomTypeRepository.save(rt);

        // 3) 응답 DTO 구성(현재 저장된 상태 그대로)
        List<SensorDto> measurements = new ArrayList<>();
        for (RoomTypeSensor s : rt.getSensors()) {
            SensorDto d = new SensorDto();
            d.setName(s.getName());
            d.setUnit(s.getUnit());

            ThresholdDto t = new ThresholdDto();

            ThresholdDto.Range c = new ThresholdDto.Range();
            c.setMin(toInt(s.getCautionMin()));
            c.setMax(toInt(s.getCautionMax()));
            t.setCaution(c);

            ThresholdDto.Range g = new ThresholdDto.Range();
            g.setMin(toInt(s.getDangerMin()));
            g.setMax(toInt(s.getDangerMax()));
            t.setDanger(g);

            ThresholdDto.Range e = new ThresholdDto.Range();
            e.setMin(toInt(s.getEmergencyMin()));
            e.setMax(toInt(s.getEmergencyMax()));
            t.setEmergency(e);

            d.setThresholds(t);
            measurements.add(d);
        }

        return new RoomTypeDetailDto(rt.getName(), measurements);
    }

    // ===== helpers =====
    private Double asDouble(Integer v) { return (v == null) ? null : v.doubleValue(); }
    private Integer toInt(Double v)     { return (v == null) ? null : v.intValue(); }
}
