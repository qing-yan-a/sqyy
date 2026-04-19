package com.sqyy.hospital.search.service;

import com.sqyy.hospital.model.HospitalModels;

import java.util.List;
import java.util.Map;

public interface SearchService {

    Map<String, Object> search(String keyword);

    List<HospitalModels.Patient> searchPatients(String keyword, int size);

    /**
     * 按药品名称/编号等关键词检索，返回匹配的药品 ID 列表（用于库存流水等筛选）。
     * 关键词为空时应返回空列表，由调用方视为“不按药品过滤”。
     */
    List<Long> searchDrugIdsByKeyword(String keyword);

    String engine();
}
