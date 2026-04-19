package com.sqyy.hospital.pharmacy.controller;

import com.sqyy.hospital.common.ApiResponse;
import com.sqyy.hospital.model.HospitalModels;
import com.sqyy.hospital.search.service.SearchSyncService;
import com.sqyy.hospital.service.HospitalPersistenceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacy")
public class PharmacyController {

    private final HospitalPersistenceService persistenceService;
    private final SearchSyncService searchSyncService;

    public PharmacyController(HospitalPersistenceService persistenceService, SearchSyncService searchSyncService) {
        this.persistenceService = persistenceService;
        this.searchSyncService = searchSyncService;
    }

    @GetMapping("/pickup-todos")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ApiResponse<List<HospitalModels.PharmacyVisitDetail>> listPickupTodos() {
        return ApiResponse.success(persistenceService.listPendingPickupVisitDetails());
    }

    @GetMapping("/injection-todos")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ApiResponse<List<HospitalModels.PharmacyVisitDetail>> listInjectionTodos(Authentication authentication) {
        return ApiResponse.success(persistenceService.listPendingInjectionVisitDetails(authentication.getName()));
    }

    @GetMapping("/visits/{visitId}")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ApiResponse<HospitalModels.PharmacyVisitDetail> detail(@PathVariable Long visitId) {
        return ApiResponse.success(persistenceService.getPharmacyVisitDetail(visitId));
    }

    @PostMapping("/visits/{visitId}/payment")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ApiResponse<HospitalModels.PharmacyVisitDetail> confirmPayment(@PathVariable Long visitId,
                                                                          Authentication authentication) {
        HospitalModels.PharmacyVisitDetail detail = persistenceService.confirmPayment(visitId, authentication.getName());
        searchSyncService.syncAll();
        return ApiResponse.success(detail);
    }

    @PostMapping("/visits/{visitId}/pickup")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ApiResponse<HospitalModels.PharmacyVisitDetail> confirmPickup(@PathVariable Long visitId,
                                                                         Authentication authentication) {
        HospitalModels.PharmacyVisitDetail detail = persistenceService.confirmPickup(visitId, authentication.getName());
        searchSyncService.syncAll();
        return ApiResponse.success(detail);
    }

    @PostMapping("/visits/{visitId}/complete-injection")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ApiResponse<HospitalModels.PharmacyVisitDetail> completeInjection(@PathVariable Long visitId,
                                                                             Authentication authentication) {
        HospitalModels.PharmacyVisitDetail detail = persistenceService.completeInjection(visitId, authentication.getName());
        searchSyncService.syncAll();
        return ApiResponse.success(detail);
    }
}
