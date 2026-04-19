package com.sqyy.hospital.search.controller;

import com.sqyy.hospital.common.ApiResponse;
import com.sqyy.hospital.model.HospitalModels;
import com.sqyy.hospital.search.service.SearchService;
import com.sqyy.hospital.search.service.SearchSyncService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;
    private final SearchSyncService searchSyncService;

    public SearchController(SearchService searchService, SearchSyncService searchSyncService) {
        this.searchService = searchService;
        this.searchSyncService = searchSyncService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PHARMACIST','RECEPTION')")
    public ApiResponse<Map<String, Object>> search(@RequestParam(defaultValue = "") String q) {
        return ApiResponse.success(searchService.search(q));
    }

    @GetMapping("/patients")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PHARMACIST','RECEPTION')")
    public ApiResponse<List<HospitalModels.Patient>> searchPatients(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "100") int size) {
        List<HospitalModels.Patient> patients = searchService.searchPatients(q, Math.min(Math.max(size, 1), 200));
        return ApiResponse.success(patients);
    }

    @PostMapping("/sync")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> sync() {
        searchSyncService.syncAll();
        return ApiResponse.success(Map.of("engine", searchService.engine(), "message", "搜索索引同步已触发"));
    }
}
