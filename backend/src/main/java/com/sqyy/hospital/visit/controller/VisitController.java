package com.sqyy.hospital.visit.controller;

import com.sqyy.hospital.common.ApiResponse;
import com.sqyy.hospital.config.DepartmentConfig;
import com.sqyy.hospital.model.HospitalModels;
import com.sqyy.hospital.search.service.SearchSyncService;
import com.sqyy.hospital.service.HospitalPersistenceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final HospitalPersistenceService persistenceService;
    private final SearchSyncService searchSyncService;
    private final DepartmentConfig departmentConfig;

    public VisitController(HospitalPersistenceService persistenceService, SearchSyncService searchSyncService, DepartmentConfig departmentConfig) {
        this.persistenceService = persistenceService;
        this.searchSyncService = searchSyncService;
        this.departmentConfig = departmentConfig;
    }

    @GetMapping("/departments")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','RECEPTION')")
    public ApiResponse<List<String>> listDepartments() {
        return ApiResponse.success(departmentConfig.getList());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','RECEPTION')")
    public ApiResponse<?> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Long patientId) {
        if (patientId != null) {
            return ApiResponse.success(persistenceService.listVisits(patientId));
        }
        if ((q == null || q.isBlank()) && page == null && size == null) {
            return ApiResponse.success(persistenceService.listVisits(null));
        }
        int pageNum = page != null && page > 0 ? page : 1;
        int pageSize = size != null && size > 0 ? Math.min(size, 100) : 15;
        return ApiResponse.success(persistenceService.searchVisits(q != null ? q : "", pageNum, pageSize));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','RECEPTION')")
    public ApiResponse<List<HospitalModels.VisitDetail>> listPending(@RequestParam String doctorName) {
        return ApiResponse.success(persistenceService.listPendingVisitsByDoctor(doctorName));
    }

    @GetMapping("/injection")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ApiResponse<List<HospitalModels.VisitDetail>> listInjectionPrescriptions() {
        return ApiResponse.success(persistenceService.listVisitsWithInjectionPrescriptions());
    }

    @GetMapping("/doctors")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','RECEPTION')")
    public ApiResponse<List<Map<String, Object>>> listDoctors(@RequestParam(required = false) String department) {
        List<Map<String, Object>> doctors = persistenceService.listDoctorUsers(department).stream()
                .map(u -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", u.id());
                    m.put("realName", u.realName());
                    m.put("username", u.username());
                    m.put("department", u.department() != null ? u.department() : "");
                    m.put("pendingCount", persistenceService.countTodayPendingByDoctor(u.realName()));
                    return m;
                })
                .toList();
        return ApiResponse.success(doctors);
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('RECEPTION')")
    public ApiResponse<HospitalModels.VisitDetail> register(@Valid @RequestBody RegisterRequest request) {
        HospitalModels.VisitDetail visit = persistenceService.createRegistration(request.patientId(), request.doctorName());
        searchSyncService.syncAll();
        return ApiResponse.success(visit);
    }

    @PostMapping("/{visitId}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTION')")
    public ApiResponse<Void> cancelVisit(@PathVariable Long visitId) {
        persistenceService.receptionCancelRegistration(visitId);
        return ApiResponse.success();
    }

    public record RegisterRequest(
            @NotNull(message = "患者不能为空") Long patientId,
            @NotBlank(message = "请选择医生") String doctorName
    ) {
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ApiResponse<HospitalModels.VisitDetail> create(@Valid @RequestBody CreateVisitRequest request) {
        HospitalModels.VisitDetail visit = persistenceService.createVisit(toCommand(request));
        searchSyncService.syncAll();
        return ApiResponse.success(visit);
    }

    @PutMapping("/{visitId}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ApiResponse<HospitalModels.VisitDetail> update(@PathVariable Long visitId,
                                                          @Valid @RequestBody CreateVisitRequest request,
                                                          Authentication authentication) {
        HospitalModels.VisitDetail visit = persistenceService.updateVisit(visitId, toCommand(request), authentication.getName());
        searchSyncService.syncAll();
        return ApiResponse.success(visit);
    }

    @DeleteMapping("/{visitId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long visitId, Authentication authentication) {
        persistenceService.deleteVisit(visitId, authentication.getName());
        searchSyncService.syncAll();
        return ApiResponse.success();
    }

    public record CreateVisitRequest(
            @NotNull(message = "患者不能为空") Long patientId,
            @NotBlank(message = "医生不能为空") String doctorName,
            @NotBlank(message = "科室不能为空") String department,
            @NotBlank(message = "主诉不能为空") String chiefComplaint,
            @NotNull(message = "就诊时间不能为空")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime visitTime,
            String notes,
            List<String> diagnoses,
            List<PrescriptionItemRequest> prescriptions
    ) {
    }

    public record PrescriptionItemRequest(
            @NotNull(message = "药品不能为空") Long drugId,
            String dosage,
            String frequency,
            @Min(value = 1, message = "用药天数至少为 1") int days,
            @Min(value = 1, message = "数量至少为 1") int quantity
    ) {
    }

    private HospitalModels.CreateVisitCommand toCommand(CreateVisitRequest request) {
        List<PrescriptionItemRequest> prescriptions = request.prescriptions() == null ? List.of() : request.prescriptions();
        return new HospitalModels.CreateVisitCommand(
                request.patientId(),
                request.doctorName(),
                request.department(),
                request.chiefComplaint(),
                request.visitTime(),
                request.notes(),
                request.diagnoses(),
                prescriptions.stream()
                        .map(item -> new HospitalModels.PrescriptionItemCommand(
                                item.drugId(),
                                item.dosage(),
                                item.frequency(),
                                item.days(),
                                item.quantity()
                        ))
                        .toList()
        );
    }
}
