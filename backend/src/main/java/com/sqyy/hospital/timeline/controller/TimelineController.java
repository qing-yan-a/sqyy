package com.sqyy.hospital.timeline.controller;

import com.sqyy.hospital.common.ApiResponse;
import com.sqyy.hospital.service.HospitalPersistenceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/timeline")
public class TimelineController {

    private final HospitalPersistenceService persistenceService;

    public TimelineController(HospitalPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @GetMapping("/patients/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ApiResponse<Map<String, Object>> patientTimeline(@PathVariable Long patientId) {
        return ApiResponse.success(persistenceService.buildTimeline(patientId));
    }
}
