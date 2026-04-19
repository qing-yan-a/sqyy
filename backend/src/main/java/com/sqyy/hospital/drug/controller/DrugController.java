package com.sqyy.hospital.drug.controller;

import com.sqyy.hospital.common.ApiResponse;
import com.sqyy.hospital.model.HospitalModels;
import com.sqyy.hospital.search.service.SearchService;
import com.sqyy.hospital.search.service.SearchSyncService;
import com.sqyy.hospital.service.HospitalPersistenceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/drugs")
public class DrugController {

    private final HospitalPersistenceService persistenceService;
    private final SearchSyncService searchSyncService;
    private final SearchService searchService;

    public DrugController(HospitalPersistenceService persistenceService,
                          SearchSyncService searchSyncService,
                          SearchService searchService) {
        this.persistenceService = persistenceService;
        this.searchSyncService = searchSyncService;
        this.searchService = searchService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PHARMACIST')")
    public ApiResponse<?> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String drugType) {
        if ((q == null || q.isBlank()) && page == null && size == null && (drugType == null || drugType.isBlank())) {
            return ApiResponse.success(persistenceService.listDrugs());
        }
        int pageNum = page != null && page > 0 ? page : 1;
        int pageSize = size != null && size > 0 ? Math.min(size, 100) : 20;
        Map<String, Object> result = persistenceService.searchDrugs(q != null ? q : "", pageNum, pageSize, drugType);
        return ApiResponse.success(result);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ApiResponse<HospitalModels.Drug> create(@Valid @RequestBody CreateDrugRequest request) {
        HospitalModels.Drug drug = persistenceService.createDrug(new HospitalModels.CreateDrugCommand(
                request.drugName(),
                request.specification(),
                request.manufacturer(),
                request.unit(),
                request.initialStock(),
                request.warningStock(),
                request.unitPrice(),
                request.drugType()
        ));
        searchSyncService.syncAll();
        return ApiResponse.success(drug);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        persistenceService.deleteDrug(id);
        searchSyncService.syncAll();
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/warning-stock")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ApiResponse<Void> updateWarningStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer warningStock = body.get("warningStock");
        if (warningStock == null || warningStock < 0) {
            throw new com.sqyy.hospital.common.BusinessException("预警值不能小于0");
        }
        persistenceService.updateDrugWarningStock(id, warningStock);
        return ApiResponse.success(null);
    }

    @GetMapping("/inventory/logs")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ApiResponse<?> listInventoryLogs(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String q) {
        if (page == null && size == null) {
            return ApiResponse.success(persistenceService.listInventoryLogs());
        }
        int pageNum = page != null && page > 0 ? page : 1;
        int pageSize = size != null && size > 0 ? Math.min(size, 100) : 15;
        String keyword = q != null ? q.trim() : "";
        if (!keyword.isEmpty()) {
            List<Long> drugIds = searchService.searchDrugIdsByKeyword(keyword);
            if (drugIds.isEmpty()) {
                return ApiResponse.success(Map.of(
                        "list", List.of(),
                        "total", 0L,
                        "page", pageNum,
                        "size", pageSize
                ));
            }
            return ApiResponse.success(persistenceService.searchInventoryLogs(pageNum, pageSize, drugIds));
        }
        return ApiResponse.success(persistenceService.searchInventoryLogs(pageNum, pageSize, null));
    }

    @PostMapping("/inventory/logs")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ApiResponse<HospitalModels.InventoryLog> adjustStock(@Valid @RequestBody AdjustStockRequest request) {
        HospitalModels.InventoryLog log = persistenceService.adjustStock(new HospitalModels.AdjustStockCommand(
                request.drugId(),
                request.changeType(),
                request.quantity(),
                request.operatorName(),
                request.remark()
        ));
        searchSyncService.syncAll();
        return ApiResponse.success(log);
    }

    public record CreateDrugRequest(
            @NotBlank(message = "药品名称不能为空") String drugName,
            @NotBlank(message = "规格不能为空") String specification,
            String manufacturer,
            @NotBlank(message = "单位不能为空") String unit,
            @Min(value = 0, message = "初始库存不能小于 0") int initialStock,
            @Min(value = 0, message = "预警库存不能小于 0") int warningStock,
            @DecimalMin(value = "0.00", message = "单价不能小于 0") BigDecimal unitPrice,
            String drugType
    ) {
    }

    public record AdjustStockRequest(
            @NotNull(message = "药品不能为空") Long drugId,
            @NotBlank(message = "变更类型不能为空") String changeType,
            @Min(value = 1, message = "数量至少为 1") int quantity,
            @NotBlank(message = "操作人不能为空") String operatorName,
            String remark
    ) {
    }
}
