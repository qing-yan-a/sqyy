package com.sqyy.hospital.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sqyy.hospital.model.HospitalModels;
import com.sqyy.hospital.search.config.SearchProperties;
import com.sqyy.hospital.search.model.SearchDocument;
import com.sqyy.hospital.service.HospitalPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@ConditionalOnBean(ElasticsearchClient.class)
@ConditionalOnProperty(prefix = "app.search", name = "engine", havingValue = "elasticsearch")
public class ElasticsearchSearchService implements SearchService, SearchSyncService {

    private static final Logger log = LoggerFactory.getLogger(ElasticsearchSearchService.class);
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final ElasticsearchClient client;
    private final ObjectMapper objectMapper;
    private final SearchProperties properties;
    private final HospitalPersistenceService persistenceService;

    public ElasticsearchSearchService(ElasticsearchClient client,
                                      ObjectMapper objectMapper,
                                      SearchProperties properties,
                                      HospitalPersistenceService persistenceService) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.persistenceService = persistenceService;
    }

    @Override
    public Map<String, Object> search(String keyword) {
        try {
            SearchResponse<SearchDocument> response = client.search(search -> search
                            .index(indexPattern())
                            .size(50)
                            .query(query -> {
                                if (keyword == null || keyword.isBlank()) {
                                    return query.matchAll(matchAll -> matchAll);
                                }
                                return query.bool(bool -> bool
                                        .should(s -> s.matchPhrase(mp -> mp.field("title").query(keyword)))
                                        .should(s -> s.term(t -> t.field("subtitle").value(keyword)))
                                );
                            }),
                    SearchDocument.class);

            List<Map<String, Object>> patients = new ArrayList<>();
            List<Map<String, Object>> visits = new ArrayList<>();
            List<Map<String, Object>> drugs = new ArrayList<>();

            List<Long> matchedPatientIds = new ArrayList<>();
            List<Long> matchedDrugIds = new ArrayList<>();

            response.hits().hits().forEach(hit -> {
                SearchDocument document = hit.source();
                if (document == null) {
                    return;
                }

                switch (document.resultType()) {
                    case "PATIENT" -> {
                        patients.add(document.payload());
                        if (document.entityId() != null) matchedPatientIds.add(document.entityId());
                    }
                    case "VISIT" -> visits.add(document.payload());
                    case "DRUG" -> {
                        drugs.add(document.payload());
                        if (document.entityId() != null) matchedDrugIds.add(document.entityId());
                    }
                    default -> {
                    }
                }
            });

            // 搜到患者 → 关联其就诊和用药
            if (!matchedPatientIds.isEmpty()) {
                for (Long patientId : matchedPatientIds) {
                    List<HospitalModels.VisitDetail> patientVisits = persistenceService.listVisits(patientId);
                    for (HospitalModels.VisitDetail vd : patientVisits) {
                        if (visits.stream().noneMatch(v -> {
                            Object id = ((Map<String, Object>) v).get("visit");
                            return id != null;
                        })) {
                            visits.add(objectMapper.convertValue(vd, MAP_TYPE));
                        }
                    }
                    // 用药：从就诊记录的处方中提取
                    for (HospitalModels.VisitDetail vd : patientVisits) {
                        for (HospitalModels.PrescriptionRecord pr : vd.prescriptions()) {
                            HospitalModels.Drug drug = persistenceService.findDrugById(pr.drugId());
                            if (drug != null && drugs.stream().noneMatch(d -> d.get("id").equals(drug.id()))) {
                                drugs.add(objectMapper.convertValue(drug, MAP_TYPE));
                            }
                        }
                    }
                }
                // 去重就诊
                List<Map<String, Object>> uniqueVisits = new ArrayList<>();
                for (Map<String, Object> v : visits) {
                    if (uniqueVisits.stream().noneMatch(u -> u.get("visit") != null && u.get("visit").equals(v.get("visit")))) {
                        uniqueVisits.add(v);
                    }
                }
                visits.clear();
                visits.addAll(uniqueVisits);
            }

            // 搜到药品 → 关联用过该药的患者和就诊
            if (!matchedDrugIds.isEmpty()) {
                List<HospitalModels.VisitDetail> allVisits = persistenceService.listVisits(null);
                for (HospitalModels.VisitDetail vd : allVisits) {
                    boolean usesDrug = vd.prescriptions().stream()
                            .anyMatch(p -> matchedDrugIds.contains(p.drugId()));
                    if (usesDrug) {
                        // 添加就诊（去重）
                        if (visits.stream().noneMatch(v -> {
                            Object visitObj = v.get("visit");
                            return visitObj instanceof Map && ((Map<?, ?>) visitObj).get("id") != null
                                    && ((Map<?, ?>) visitObj).get("id").equals(vd.visit().id());
                        })) {
                            visits.add(objectMapper.convertValue(vd, MAP_TYPE));
                        }
                        // 添加患者（去重）
                        if (vd.patient() != null && patients.stream().noneMatch(p -> p.get("id").equals(vd.patient().id()))) {
                            patients.add(objectMapper.convertValue(vd.patient(), MAP_TYPE));
                        }
                    }
                }
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("keyword", keyword);
            result.put("patients", patients);
            result.put("visits", visits);
            result.put("drugs", drugs);
            result.put("engine", engine());
            return result;
        } catch (IOException | ElasticsearchException exception) {
            log.warn("Elasticsearch search failed, falling back to memory search: {}", exception.getMessage());
            Map<String, Object> result = new LinkedHashMap<>(persistenceService.search(keyword));
            result.put("engine", "memory-fallback");
            return result;
        }
    }

    @Override
    public List<Long> searchDrugIdsByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        try {
            SearchResponse<SearchDocument> response = client.search(search -> search
                            .index(properties.getElasticsearch().getIndexPrefix() + "-drug")
                            .size(200)
                            .query(query -> query.bool(bool -> bool
                                    .should(s -> s.matchPhrase(mp -> mp.field("title").query(keyword.trim())))
                                    .should(s -> s.term(t -> t.field("subtitle").value(keyword.trim())))
                            )),
                    SearchDocument.class);

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .filter(doc -> doc != null && "DRUG".equals(doc.resultType()) && doc.entityId() != null)
                    .map(SearchDocument::entityId)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (IOException | ElasticsearchException exception) {
            log.warn("Elasticsearch drug id search failed, falling back to DB: {}", exception.getMessage());
            return fallbackDrugIdsFromDb(keyword.trim());
        }
    }

    private List<Long> fallbackDrugIdsFromDb(String keyword) {
        Map<String, Object> res = persistenceService.searchDrugs(keyword, 1, 200, null);
        @SuppressWarnings("unchecked")
        List<HospitalModels.Drug> list = (List<HospitalModels.Drug>) res.get("list");
        if (list == null) {
            return List.of();
        }
        return list.stream().map(HospitalModels.Drug::id).distinct().toList();
    }

    @Override
    public List<HospitalModels.Patient> searchPatients(String keyword, int size) {
        try {
            SearchResponse<SearchDocument> response = client.search(search -> search
                            .index(properties.getElasticsearch().getIndexPrefix() + "-patient")
                            .size(Math.min(Math.max(size, 1), 200))
                            .query(query -> {
                                if (keyword == null || keyword.isBlank()) {
                                    return query.matchAll(matchAll -> matchAll);
                                }
                                return query.bool(bool -> bool
                                        .should(s -> s.matchPhrase(mp -> mp.field("title").query(keyword)))
                                        .should(s -> s.term(t -> t.field("subtitle").value(keyword)))
                                );
                            }),
                    SearchDocument.class);

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .filter(doc -> doc != null && "PATIENT".equals(doc.resultType()))
                    .map(doc -> objectMapper.convertValue(doc.payload(), HospitalModels.Patient.class))
                    .toList();
        } catch (IOException | ElasticsearchException exception) {
            log.warn("Elasticsearch patient search failed, falling back to memory: {}", exception.getMessage());
            return persistenceService.search(keyword != null ? keyword : "").entrySet().stream()
                    .filter(e -> "patients".equals(e.getKey()))
                    .flatMap(e -> ((List<?>) e.getValue()).stream())
                    .map(p -> (HospitalModels.Patient) p)
                    .limit(size)
                    .toList();
        }
    }

    @Override
    public String engine() {
        return "elasticsearch";
    }

    @Override
    public void syncAll() {
        if (!properties.getElasticsearch().isEnabled()) {
            return;
        }

        try {
            deleteExistingIndices();
            for (HospitalModels.Patient patient : persistenceService.listPatients()) {
                indexDocument(patientDocument(patient));
            }
            for (HospitalModels.VisitDetail visit : persistenceService.listVisits(null)) {
                indexDocument(visitDocument(visit));
            }
            for (HospitalModels.Drug drug : persistenceService.listDrugs()) {
                indexDocument(drugDocument(drug));
            }
            refreshIndices();
        } catch (IOException | ElasticsearchException exception) {
            log.warn("Elasticsearch sync skipped: {}", exception.getMessage());
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void syncOnStartup() {
        if (properties.isAutoSyncOnStartup()) {
            syncAll();
        }
    }

    private void indexDocument(SearchDocument document) throws IOException {
        IndexRequest<SearchDocument> request = IndexRequest.of(index -> index
                .index(indexName(document.resultType()))
                .id(document.documentId())
                .document(document));
        client.index(request);
    }

    private void refreshIndices() throws IOException {
        client.indices().refresh(refresh -> refresh.index(indexPattern()));
    }

    private void deleteExistingIndices() {
        try {
            client.indices().delete(delete -> delete.index(indexPattern()));
        } catch (IOException | ElasticsearchException exception) {
            log.info("Skip deleting old Elasticsearch indices: {}", exception.getMessage());
        }
    }

    private SearchDocument patientDocument(HospitalModels.Patient patient) {
        return new SearchDocument(
                "patient-" + patient.id(),
                "PATIENT",
                patient.id(),
                patient.name(),
                patient.patientNo(),
                String.join(" ", safe(patient.phone()), safe(patient.idCard()), safe(patient.address()),
                        safe(patient.allergyHistory()), safe(patient.medicalHistory())),
                List.of(patient.gender(), patient.patientNo()),
                objectMapper.convertValue(patient, MAP_TYPE),
                LocalDateTime.now().toString()
        );
    }

    private SearchDocument visitDocument(HospitalModels.VisitDetail visitDetail) {
        List<String> diagnosisNames = visitDetail.diagnoses().stream()
                .map(HospitalModels.DiagnosisRecord::diagnosisName)
                .toList();
        List<String> prescriptionNames = visitDetail.prescriptions().stream()
                .map(HospitalModels.PrescriptionRecord::drugName)
                .toList();

        return new SearchDocument(
                "visit-" + visitDetail.visit().id(),
                "VISIT",
                visitDetail.visit().id(),
                visitDetail.patient() != null
                        ? visitDetail.patient().name() + " / " + visitDetail.visit().department()
                        : visitDetail.visit().department(),
                visitDetail.visit().chiefComplaint(),
                String.join(" ", safe(visitDetail.visit().doctorName()), safe(visitDetail.visit().notes()),
                        String.join(" ", diagnosisNames), String.join(" ", prescriptionNames)),
                diagnosisNames,
                objectMapper.convertValue(visitDetail, MAP_TYPE),
                LocalDateTime.now().toString()
        );
    }

    private SearchDocument drugDocument(HospitalModels.Drug drug) {
        return new SearchDocument(
                "drug-" + drug.id(),
                "DRUG",
                drug.id(),
                drug.drugName(),
                drug.drugCode(),
                String.join(" ", safe(drug.specification()), safe(drug.manufacturer()), safe(drug.unit())),
                List.of(drug.drugCode(), String.valueOf(drug.stock())),
                objectMapper.convertValue(drug, MAP_TYPE),
                LocalDateTime.now().toString()
        );
    }

    private String indexName(String resultType) {
        return properties.getElasticsearch().getIndexPrefix() + "-" + resultType.toLowerCase();
    }

    private String indexPattern() {
        return properties.getElasticsearch().getIndexPrefix() + "-*";
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
