package org.smartcampus.smartcampus_be.domain.sensor.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.sensor.dto.request.CreateSensorRequest;
import org.smartcampus.smartcampus_be.domain.sensor.dto.request.DeleteSensorRequest;
import org.smartcampus.smartcampus_be.domain.sensor.dto.response.DataTypeResponse;
import org.smartcampus.smartcampus_be.domain.sensor.dto.response.PagingSensorResponse;
import org.smartcampus.smartcampus_be.domain.sensor.dto.response.SensorResponse;
import org.smartcampus.smartcampus_be.domain.sensor.service.SensorService;
import org.smartcampus.smartcampus_be.global.response.ApiResponse;
import org.smartcampus.smartcampus_be.global.response.SuccessType;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sensors")
public class SensorController {

    private final SensorService sensorService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ApiResponse<SensorResponse> createSensor(@RequestBody @Valid CreateSensorRequest request) {
        SensorResponse response = sensorService.createSensor(request);
        return ApiResponse.success(SuccessType.SENSOR_CREATE_SUCCESS, response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ApiResponse<PagingSensorResponse> getSensors(
            @RequestParam(value = "roomNumber", required = false) String roomNumber,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        PagingSensorResponse response = sensorService.getAllSensors(roomNumber, PageRequest.of(page, size));
        return ApiResponse.success(SuccessType.SENSOR_LIST_SUCCESS, response);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete")
    public ApiResponse<?> deleteSensor(@RequestBody DeleteSensorRequest request) {
        sensorService.deleteSensor(request);
        return ApiResponse.success(SuccessType.SENSOR_DELETE_SUCCESS);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/data-types")
    public ApiResponse<List<DataTypeResponse>> getSensorDataTypes() {
        List<DataTypeResponse> responses = sensorService.getSensorDataTypes();
        return ApiResponse.success(SuccessType.SENSOR_DATA_TYPES_SUCCESS, responses);
    }
}
