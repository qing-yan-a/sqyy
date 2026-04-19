package com.sqyy.hospital.search.service;

import com.sqyy.hospital.model.HospitalModels;
import com.sqyy.hospital.service.HospitalPersistenceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@ConditionalOnMissingBean(SearchService.class)
public class MemorySearchService implements SearchService, SearchSyncService {

    private final HospitalPersistenceService persistenceService;

    public MemorySearchService(HospitalPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @Override
    public Map<String, Object> search(String keyword) {
        Map<String, Object> result = new LinkedHashMap<>(persistenceService.search(keyword));
        result.put("engine", engine());
        return result;
    }

    @Override
    public List<Long> searchDrugIdsByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        Map<String, Object> res = persistenceService.searchDrugs(keyword.trim(), 1, 200, null);
        @SuppressWarnings("unchecked")
        List<HospitalModels.Drug> list = (List<HospitalModels.Drug>) res.get("list");
        if (list == null) {
            return List.of();
        }
        return list.stream().map(HospitalModels.Drug::id).distinct().collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<HospitalModels.Patient> searchPatients(String keyword, int size) {
        List<HospitalModels.Patient> patients = (List<HospitalModels.Patient>) persistenceService.search(keyword).get("patients");
        return patients == null ? List.of() : patients.stream().limit(size).toList();
    }

    @Override
    public String engine() {
        return "memory";
    }

    @Override
    public void syncAll() {
        // Memory mode does not require external index synchronization.
    }
}
