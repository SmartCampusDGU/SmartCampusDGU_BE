package org.smartcampus.smartcampus_be.domain.sensor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smartcampus.smartcampus_be.domain.sensor.dto.request.CreateSensorRequest;
import org.smartcampus.smartcampus_be.domain.sensor.dto.response.SensorResponse;
import org.smartcampus.smartcampus_be.domain.sensor.service.SensorService;
import org.smartcampus.smartcampus_be.global.response.ApiResponse;
import org.smartcampus.smartcampus_be.global.response.SuccessType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
