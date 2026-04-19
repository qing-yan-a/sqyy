package com.sqyy.hospital.search.model;

import java.util.List;
import java.util.Map;

public record SearchDocument(
        String documentId,
        String resultType,
        Long entityId,
        String title,
        String subtitle,
        String content,
        List<String> tags,
        Map<String, Object> payload,
        String updatedAt
) {
}
