package org.smartcampus.smartcampus_be.service;

import org.smartcampus.smartcampus_be.domain.Room;
import org.smartcampus.smartcampus_be.domain.RoomSensor;
import org.smartcampus.smartcampus_be.dto.CreateRoomRequestDto;
import org.smartcampus.smartcampus_be.dto.RoomDetailDto;
import org.smartcampus.smartcampus_be.dto.RoomDetailDto.MeasurementDto;
import org.smartcampus.smartcampus_be.dto.RoomListItemDto;
import org.smartcampus.smartcampus_be.dto.UpdateRoomRequestDto;
import org.smartcampus.smartcampus_be.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // 공간 생성 //
    @Transactional
    public Long createRoom(CreateRoomRequestDto req) {
        if (roomRepository.existsByRoomNumber(req.getRoomNumber())) {
            throw new IllegalArgumentException("이미 존재하는 강의실 번호입니다.");
        }

        Room room = new Room();
        room.setRoomNumber(req.getRoomNumber());
        room.setRoomType(req.getRoomType());

        for (var s : req.getSensors()) {
            if (!s.isUseDefault() && s.getThresholds() == null) {
                throw new IllegalArgumentException("수동 임계값 입력 시 thresholds가 필요합니다.");
            }

            RoomSensor sensor = new RoomSensor();
            sensor.setName(s.getName());
            sensor.setUseDefault(s.isUseDefault());

            if (!s.isUseDefault() && s.getThresholds() != null) {
                var t = s.getThresholds();
                if (t.getCaution() != null) {
                    // Integer/Double 어느 타입이든 대응
                    setNum(sensor, "setCautionMin", toD(t.getCaution().getMin()));
                    setNum(sensor, "setCautionMax", toD(t.getCaution().getMax()));
                }
                if (t.getDanger() != null) {
                    setNum(sensor, "setDangerMin", toD(t.getDanger().getMin()));
                    setNum(sensor, "setDangerMax", toD(t.getDanger().getMax()));
                }
                if (t.getEmergency() != null) {
                    setNum(sensor, "setEmergencyMin", toD(t.getEmergency().getMin()));
                    setNum(sensor, "setEmergencyMax", toD(t.getEmergency().getMax()));
                }
            }

            room.addSensor(sensor);
        }

        Room saved = roomRepository.save(room);
        return saved.getId();
    }

    // 공간 목록 조회 (옵션: roomType 필터) //
    @Transactional(readOnly = true)
    public List<RoomListItemDto> getRooms(String roomType) {
        List<Room> rooms = (roomType == null || roomType.isBlank())
                ? roomRepository.findAll()
                : roomRepository.findByRoomType(roomType);

        return rooms.stream()
                .map(room -> new RoomListItemDto(
                        room.getId(),
                        room.getRoomType(),
                        room.getSensors().stream()
                                .filter(s -> !toBool(s.isUseDefault()))
                                .map(RoomSensor::getName)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    //공간 상세 조회 //
    @Transactional(readOnly = true)
    public RoomDetailDto getRoomDetail(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 공간입니다."));
        return buildDetail(room);
    }

    // 공간 수정 //
    @Transactional
    public RoomDetailDto updateRoom(Long roomId, UpdateRoomRequestDto req) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NoSuchElementException("해당 공간을 찾을 수 없습니다."));

        // 1) roomType 변경
        room.setRoomType(req.getRoomType());

        // 2) measurements 치환
        room.getSensors().clear();
        for (var m : req.getMeasurements()) {
            RoomSensor s = new RoomSensor();
            s.setRoom(room);
            s.setName(m.getName());

            // unit (있을 때만 세팅)
            setUnitIfExists(s, m.getUnit());

            var th = m.getThresholds();
            if (th == null) {
                s.setUseDefault(true);
            } else {
                s.setUseDefault(false);
                applyPair(th.get("주의"), (a, b) -> {
                    setNum(s, "setCautionMin", a);
                    setNum(s, "setCautionMax", b);
                });
                applyPair(th.get("위험"), (a, b) -> {
                    setNum(s, "setDangerMin", a);
                    setNum(s, "setDangerMax", b);
                });
                applyPair(th.get("응급"), (a, b) -> {
                    setNum(s, "setEmergencyMin", a);
                    setNum(s, "setEmergencyMax", b);
                });
            }
            room.addSensor(s);
        }

        Room saved = roomRepository.save(room);
        return buildDetail(saved);
    }

    // ---------- helpers ----------

    private boolean toBool(Boolean b) { return b != null && b; }

    private static Double toD(Number n) { return (n == null) ? null : n.doubleValue(); }

    /** 상세 응답 변환 공통 헬퍼 */
    private RoomDetailDto buildDetail(Room room) {
        List<MeasurementDto> measurements = room.getSensors().stream()
                .map(s -> {
                    String unit = extractUnitIfExists(s);
                    Map<String, List<Double>> thresholds = new LinkedHashMap<>();
                    thresholds.put("주의", Arrays.asList(toD(getNum(s, "getCautionMin")),   toD(getNum(s, "getCautionMax"))));
                    thresholds.put("위험", Arrays.asList(toD(getNum(s, "getDangerMin")),    toD(getNum(s, "getDangerMax"))));
                    thresholds.put("응급", Arrays.asList(toD(getNum(s, "getEmergencyMin")), toD(getNum(s, "getEmergencyMax"))));
                    return new MeasurementDto(s.getName(), unit, thresholds);
                })
                .collect(Collectors.toList());

        return new RoomDetailDto(room.getId(), room.getRoomType(), measurements);
    }

    /** RoomSensor에 getUnit()이 있으면 값, 없으면 "" 반환 */
    private static String extractUnitIfExists(RoomSensor s) {
        try {
            var m = s.getClass().getMethod("getUnit");
            Object v = m.invoke(s);
            return v == null ? "" : v.toString();
        } catch (Exception ignore) { return ""; }
    }

    /** RoomSensor에 setUnit(String)이 있으면 호출 */
    private static void setUnitIfExists(RoomSensor s, String unit) {
        try {
            var m = s.getClass().getMethod("setUnit", String.class);
            m.invoke(s, unit == null ? "" : unit);
        } catch (Exception ignore) { /* unit 필드가 없으면 무시 */ }
    }

    /** 숫자 getter(타입 무관) */
    private static Number getNum(RoomSensor s, String getterName) {
        try {
            var m = s.getClass().getMethod(getterName);
            Object v = m.invoke(s);
            return (v instanceof Number) ? (Number) v : null;
        } catch (Exception e) { return null; }
    }

    /** 세터 파라미터가 Double/Integer/double/int 어느 것이든 맞춰 호출 */
    private static void setNum(RoomSensor target, String setterName, Double value) {
        try {
            // 1) Double
            try {
                var m = target.getClass().getMethod(setterName, Double.class);
                m.invoke(target, value);
                return;
            } catch (NoSuchMethodException ignore) {}

            // 2) Integer
            try {
                var m = target.getClass().getMethod(setterName, Integer.class);
                m.invoke(target, value == null ? null : value.intValue());
                return;
            } catch (NoSuchMethodException ignore) {}

            // 3) primitive double
            try {
                var m = target.getClass().getMethod(setterName, double.class);
                m.invoke(target, value == null ? 0.0 : value);
                return;
            } catch (NoSuchMethodException ignore) {}

            // 4) primitive int
            try {
                var m = target.getClass().getMethod(setterName, int.class);
                m.invoke(target, value == null ? 0 : value.intValue());
            } catch (NoSuchMethodException ignore) {}
        } catch (Exception ignore) { /* no-op */ }
    }

    private interface PairSetter { void set(Double a, Double b); }
    private void applyPair(List<Double> pair, PairSetter setter) {
        if (pair == null) return;
        Double a = pair.size() > 0 ? pair.get(0) : null;
        Double b = pair.size() > 1 ? pair.get(1) : null;
        setter.set(a, b);
    }
}
