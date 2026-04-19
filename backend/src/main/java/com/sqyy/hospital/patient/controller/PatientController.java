package com.sqyy.hospital.patient.controller;

import com.sqyy.hospital.common.ApiResponse;
import com.sqyy.hospital.model.HospitalModels;
import com.sqyy.hospital.search.service.SearchSyncService;
import com.sqyy.hospital.service.HospitalPersistenceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final HospitalPersistenceService persistenceService;
    private final SearchSyncService searchSyncService;

    public PatientController(HospitalPersistenceService persistenceService, SearchSyncService searchSyncService) {
        this.persistenceService = persistenceService;
        this.searchSyncService = searchSyncService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','RECEPTION')")
    public ApiResponse<?> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if ((q == null || q.isBlank()) && page == null && size == null) {
            return ApiResponse.success(persistenceService.listPatients());
        }
        int pageNum = page != null && page > 0 ? page : 1;
        int pageSize = size != null && size > 0 ? Math.min(size, 100) : 15;
        return ApiResponse.success(persistenceService.searchPatients(q != null ? q : "", pageNum, pageSize));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTION','DOCTOR')")
    public ApiResponse<HospitalModels.Patient> create(@Valid @RequestBody CreatePatientRequest request) {
        HospitalModels.Patient patient = persistenceService.createPatient(new HospitalModels.CreatePatientCommand(
                request.name(),
                request.gender(),
                request.age(),
                request.phone(),
                request.idCard(),
                request.address(),
                request.allergyHistory(),
                request.medicalHistory()
        ));
        searchSyncService.syncAll();
        return ApiResponse.success(patient);
    }

    @PutMapping("/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','RECEPTION')")
    public ApiResponse<HospitalModels.Patient> update(
            @PathVariable Long patientId,
            Authentication authentication,
            @Valid @RequestBody CreatePatientRequest request) {
        HospitalModels.Patient patient = persistenceService.updatePatient(patientId, new HospitalModels.CreatePatientCommand(
                request.name(),
                request.gender(),
                request.age(),
                request.phone(),
                request.idCard(),
                request.address(),
                request.allergyHistory(),
                request.medicalHistory()
        ), authentication.getName());
        searchSyncService.syncAll();
        return ApiResponse.success(patient);
    }

    public record CreatePatientRequest(
            @NotBlank(message = "姓名不能为空") String name,
            @NotBlank(message = "性别不能为空") String gender,
            @Min(value = 0, message = "年龄不能小于 0") int age,
            String phone,
            String idCard,
            String address,
            String allergyHistory,
            String medicalHistory
    ) {
    }
}
