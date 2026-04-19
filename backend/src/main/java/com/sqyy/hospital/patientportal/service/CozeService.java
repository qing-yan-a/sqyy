package com.sqyy.hospital.patientportal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sqyy.hospital.config.CozeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CozeService {

    private static final Logger log = LoggerFactory.getLogger(CozeService.class);
    private static final int MAX_RETRIES = 120;

    private final CozeProperties cozeProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public CozeService(CozeProperties cozeProperties, ObjectMapper objectMapper) {
        this.cozeProperties = cozeProperties;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
        // Coze 接口响应慢，设置超时
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(120000);
        this.restTemplate.setRequestFactory(factory);
    }

    /**
     * AI 助手对话
     */
    public String chatWithAI(String userId, String message) {
        return chat(cozeProperties.getAiBotId(), userId, message);
    }

    /**
     * 健康资讯（固定发送"今日资讯"）- 返回结构化 JSON 数组字符串
     */
    public String fetchHealthNews(String userId) {
        try {
            String reply = chat(cozeProperties.getNewsBotId(), userId, "今日资讯");
            if (reply == null || reply.isBlank()) {
                log.warn("Coze 健康资讯返回为空");
                return fallbackNewsJson("AI 未返回内容");
            }
            // 如果返回的是错误消息（不是 JSON），直接兜底
            String trimmed = reply.trim();
            if (!trimmed.contains("[") && !trimmed.contains("{")) {
                log.warn("Coze 健康资讯返回非 JSON 内容: {}", trimmed.substring(0, Math.min(100, trimmed.length())));
                return fallbackNewsJson(trimmed);
            }
            return extractJsonArray(reply);
        } catch (Exception e) {
            log.error("Coze 健康资讯调用异常", e);
            return fallbackNewsJson("AI 服务异常: " + e.getMessage());
        }
    }

    /**
     * 通用 Coze 聊天：发起对话 → 轮询结果 → 返回 AI 回复
     */
    private String chat(String botId, String userId, String message) {
        try {
            if (isBlank(cozeProperties.getPat())) {
                log.warn("Coze PAT 未配置");
                return "抱歉，AI 服务未完成配置，请联系管理员。";
            }
            if (isBlank(botId)) {
                log.warn("Coze botId 未配置, userId={}", userId);
                return "抱歉，AI 服务未完成配置，请联系管理员。";
            }

            // 1. 发起对话
            ChatSession session = startConversation(botId, userId, message);
            if (session == null) {
                return "抱歉，AI 服务暂时不可用，请稍后重试。";
            }

            // 2. 轮询对话状态，等待完成
            waitForConversation(session);

            // 3. 拉取消息列表
            return getAssistantReply(session);

        } catch (RestClientResponseException e) {
            String errorMessage = extractCozeErrorMessage(e.getResponseBodyAsString(), e.getStatusText());
            log.error("Coze API HTTP 异常: botId={}, status={}, body={}", botId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            return errorMessage;
        } catch (Exception e) {
            log.error("Coze API 调用失败: botId={}, error={}", botId, e.getMessage(), e);
            return firstNonBlank(e.getMessage(), "抱歉，AI 服务出现异常，请稍后重试。");
        }
    }

    /**
     * 发起对话
     */
    private ChatSession startConversation(String botId, String userId, String message) throws Exception {
        String url = buildApiUrl("/v3/chat");

        Map<String, Object> body = new HashMap<>();
        body.put("bot_id", botId);
        body.put("user_id", userId);
        body.put("additional_messages", List.of(
                Map.of("role", "user", "content", message, "content_type", "text")
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(cozeProperties.getPat());

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Coze chat start failed: {}", response.getBody());
            throw new RuntimeException(extractCozeErrorMessage(response.getBody(), "Coze chat start failed"));
        }

        JsonNode root = objectMapper.readTree(response.getBody());
        ensureSuccess(root, "发起对话失败");
        JsonNode data = root.path("data");
        String conversationId = data.path("conversation_id").asText(null);
        String chatId = firstNonBlank(
                data.path("id").asText(null),
                data.path("chat_id").asText(null)
        );
        if (isBlank(conversationId) || isBlank(chatId)) {
            throw new RuntimeException("发起对话成功但未获取到 chat_id 或 conversation_id");
        }
        log.info("Coze conversation started: conversationId={}, chatId={}", conversationId, chatId);
        return new ChatSession(botId, conversationId, chatId);
    }

    /**
     * 轮询对话状态直到完成
     */
    private void waitForConversation(ChatSession session) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(cozeProperties.getPat());
        HttpEntity<Void> request = new HttpEntity<>(headers);

        for (int i = 0; i < MAX_RETRIES; i++) {
            String url = buildApiUrl("/v3/chat/retrieve?conversation_id=" + session.conversationId()
                    + "&chat_id=" + session.chatId());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            int code = root.path("code").asInt(0);
            if (code != 0) {
                String msg = root.path("msg").asText("unknown");
                log.error("Coze retrieve error: code={}, msg={}, body={}", code, msg, response.getBody());
                throw new RuntimeException("Coze retrieve error: " + msg);
            }
            JsonNode data = root.path("data");
            String status = firstNonBlank(
                    data.path("status").asText(""),
                    data.path("chat_status").asText("")
            );

            log.info("Coze status: {} (attempt {}/{})", status, i + 1, MAX_RETRIES);

            if ("completed".equals(status)) {
                log.info("Coze completed: {}", session.conversationId());
                return;
            }
            if ("failed".equals(status)) {
                log.error("Coze failed: {}, body={}", session.conversationId(), response.getBody());
                throw new RuntimeException("Coze conversation failed");
            }

            Thread.sleep(1000);
        }
        throw new RuntimeException("Coze conversation timeout after " + MAX_RETRIES + "s");
    }

    /**
     * 拉取 AI 回复
     */
    private String getAssistantReply(ChatSession session) throws Exception {
        String url = buildApiUrl("/v3/chat/message/list?conversation_id=" + session.conversationId()
                + "&chat_id=" + session.chatId());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(cozeProperties.getPat());
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        ensureSuccess(root, "获取回复消息失败");
        // v3 API: data 直接是消息数组
        JsonNode messages = root.path("data");
        if (!messages.isArray()) {
            return "抱歉，未获取到回复内容。";
        }

        // 取最后一条 assistant 角色的回复（过滤掉 follow_up 类型）
        String lastReply = null;
        for (JsonNode msg : messages) {
            String role = msg.path("role").asText("");
            String type = msg.path("type").asText("");
            if ("assistant".equals(role) && !"follow_up".equals(type) && !"verbose".equals(type)) {
                String content = extractMessageContent(msg);
                if (!isBlank(content)) {
                    lastReply = content;
                }
            }
        }
        return lastReply != null ? lastReply : "抱歉，未获取到有效回复。";
    }

    private String extractMessageContent(JsonNode msg) {
        String content = msg.path("content").asText("");
        if (isBlank(content)) {
            return "";
        }

        String trimmed = content.trim();
        if ((trimmed.startsWith("{") && trimmed.endsWith("}")) || (trimmed.startsWith("[") && trimmed.endsWith("]"))) {
            return trimmed;
        }

        try {
            JsonNode contentNode = objectMapper.readTree(trimmed);
            if (contentNode.isTextual()) {
                return contentNode.asText("");
            }
        } catch (Exception ignored) {
            // content 本身就是普通文本，忽略 JSON 解析异常
        }
        return trimmed;
    }

    private String normalizeNewsReply(String reply) {
        if (isBlank(reply)) {
            return "[]";
        }

        String trimmed = reply.trim();
        if (trimmed.startsWith("```")) {
            int firstNewLine = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstNewLine >= 0 && lastFence > firstNewLine) {
                trimmed = trimmed.substring(firstNewLine + 1, lastFence).trim();
            }
        }

        int arrayStart = trimmed.indexOf('[');
        int arrayEnd = trimmed.lastIndexOf(']');
        if (arrayStart >= 0 && arrayEnd > arrayStart) {
            String candidate = trimmed.substring(arrayStart, arrayEnd + 1).trim();
            try {
                JsonNode node = objectMapper.readTree(candidate);
                if (node.isArray()) {
                    return candidate;
                }
            } catch (Exception ignored) {
                // 继续走兜底逻辑
            }
        }

        return trimmed;
    }

    /**
     * 从 Coze 回复中提取合法 JSON 数组，校验结构并兜底
     */
    private String extractJsonArray(String reply) {
        if (isBlank(reply)) {
            log.warn("Coze 健康资讯回复为空");
            return fallbackNewsJson("AI 未返回内容");
        }

        String trimmed = reply.trim();
        log.info("Coze 健康资讯原始回复（前300字）: {}", trimmed.substring(0, Math.min(300, trimmed.length())));

        // 去掉 markdown 代码块
        if (trimmed.startsWith("```")) {
            int firstNewLine = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstNewLine >= 0 && lastFence > firstNewLine) {
                trimmed = trimmed.substring(firstNewLine + 1, lastFence).trim();
            }
        }

        // 提取 [ ... ] 部分
        int arrayStart = trimmed.indexOf('[');
        int arrayEnd = trimmed.lastIndexOf(']');
        if (arrayStart < 0 || arrayEnd <= arrayStart) {
            log.warn("Coze 健康资讯未包含 JSON 数组");
            return fallbackNewsJson("AI 返回格式异常");
        }

        String candidate = trimmed.substring(arrayStart, arrayEnd + 1).trim();
        try {
            JsonNode node = objectMapper.readTree(candidate);
            if (!node.isArray() || node.isEmpty()) {
                log.warn("Coze 健康资讯解析结果为空数组或非数组");
                return fallbackNewsJson("AI 返回空数据");
            }

            // 校验每个资讯条目的字段完整性，缺失字段补空字符串
            String[] requiredFields = {"title", "summary", "image", "url", "source"};
            for (int i = 0; i < node.size(); i++) {
                JsonNode item = node.get(i);
                if (!item.isObject()) continue;
                var obj = (com.fasterxml.jackson.databind.node.ObjectNode) item;
                for (String field : requiredFields) {
                    if (obj.get(field) == null || obj.get(field).isNull()) {
                        obj.put(field, "");
                    }
                }
            }

            String result = objectMapper.writeValueAsString(node);
            log.info("Coze 健康资讯解析成功，共 {} 条", node.size());
            return result;
        } catch (Exception e) {
            log.error("Coze 健康资讯 JSON 解析失败: {}", e.getMessage());
            return fallbackNewsJson("JSON 解析失败");
        }
    }

    private String fallbackNewsJson(String reason) {
        try {
            return objectMapper.writeValueAsString(List.of(
                    Map.of("title", "资讯加载失败", "summary", reason + "，请稍后重试", "image", "", "url", "", "source", "系统")
            ));
        } catch (Exception e) {
            return "[]";
        }
    }

    private void ensureSuccess(JsonNode root, String fallbackMessage) {
        int code = root.path("code").asInt(0);
        if (code == 0) {
            return;
        }

        String message = firstNonBlank(
                root.path("msg").asText(null),
                root.path("message").asText(null),
                fallbackMessage
        );
        throw new RuntimeException(message);
    }

    private String extractCozeErrorMessage(String responseBody, String fallbackMessage) {
        if (isBlank(responseBody)) {
            return firstNonBlank(fallbackMessage, "AI 服务调用失败");
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return firstNonBlank(
                    root.path("msg").asText(null),
                    root.path("message").asText(null),
                    root.path("error").asText(null),
                    responseBody
            );
        } catch (Exception ignored) {
            return responseBody;
        }
    }

    private String buildApiUrl(String path) {
        String baseUrl = cozeProperties.getApiBaseUrl();
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + path;
        }
        return baseUrl + path;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record ChatSession(String botId, String conversationId, String chatId) {}
}
