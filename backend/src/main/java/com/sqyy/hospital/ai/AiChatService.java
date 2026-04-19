package com.sqyy.hospital.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sqyy.hospital.common.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.ai.mimo.api-key:}")
    private String apiKey;

    @Value("${app.ai.mimo.model:mimo-v2-flash}")
    private String model;

    @Value("${app.ai.mimo.base-url:https://api.xiaomimimo.com/v1}")
    private String baseUrl;

    public String chat(String systemPrompt, List<ChatMessage> history, String userMessage) {
        String key = apiKey == null ? "" : apiKey.trim();
        if (key.isBlank()) {
            throw new BusinessException(
                    "AI 服务未配置 API Key。请在小米 MiMo 开放平台创建密钥（"
                            + "https://platform.xiaomimimo.com/#/console/api-keys ），"
                            + "并设置环境变量 MIMO_API_KEY 或配置 app.ai.mimo.api-key。");
        }

        try {
            ArrayNode messages = objectMapper.createArrayNode();

            if (systemPrompt != null && !systemPrompt.isBlank()) {
                ObjectNode sys = objectMapper.createObjectNode();
                sys.put("role", "system");
                sys.put("content", systemPrompt);
                messages.add(sys);
            }

            if (history != null) {
                for (ChatMessage msg : history) {
                    ObjectNode m = objectMapper.createObjectNode();
                    m.put("role", msg.role());
                    m.put("content", msg.content());
                    messages.add(m);
                }
            }

            ObjectNode userMsg = objectMapper.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);

            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            requestBody.set("messages", messages);
            requestBody.put("max_tokens", 1024);
            requestBody.put("temperature", 0.7);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(key);

            String url = baseUrl.endsWith("/") ? baseUrl + "chat/completions" : baseUrl + "/chat/completions";
            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                return choices.get(0).path("message").path("content").asText("抱歉，我暂时无法回答这个问题。");
            }
            return "抱歉，AI 服务返回异常，请稍后重试。";
        } catch (HttpClientErrorException e) {
            String detail = extractUpstreamErrorMessage(e);
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                log.warn("MiMo API 401: {}", detail != null ? detail : e.getMessage());
                throw new BusinessException(
                        "小米 MiMo 鉴权失败（401）。请到开放平台核对 API Key："
                                + "https://platform.xiaomimimo.com/#/console/api-keys ，"
                                + "并通过环境变量 MIMO_API_KEY 或 app.ai.mimo.api-key 配置。"
                                + (detail != null ? " 上游说明：" + detail : ""));
            }
            log.error("AI chat HTTP {}: {}", e.getStatusCode(), detail, e);
            throw new BusinessException("AI 服务调用失败: " + e.getStatusCode()
                    + (detail != null ? " — " + detail : ""));
        } catch (Exception e) {
            log.error("AI chat error: {}", e.getMessage(), e);
            throw new BusinessException("AI 服务调用失败: " + e.getMessage());
        }
    }

    /** 兼容 OpenAI 风格：{"error":{"message":"..."}} */
    private String extractUpstreamErrorMessage(HttpClientErrorException e) {
        try {
            String body = e.getResponseBodyAsString(StandardCharsets.UTF_8);
            if (body == null || body.isBlank()) {
                return null;
            }
            JsonNode root = objectMapper.readTree(body);
            JsonNode msg = root.path("error").path("message");
            return msg.isTextual() ? msg.asText() : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    public record ChatMessage(String role, String content) {}
}
