package com.sqyy.hospital.dashboard.controller;

import com.sqyy.hospital.common.ApiResponse;
import com.sqyy.hospital.service.HospitalPersistenceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final HospitalPersistenceService persistenceService;

    public DashboardController(HospitalPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @GetMapping("/reception")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTION')")
    public ApiResponse<Map<String, Object>> receptionDashboard() {
        return ApiResponse.success(persistenceService.getReceptionDashboard());
    }
}
