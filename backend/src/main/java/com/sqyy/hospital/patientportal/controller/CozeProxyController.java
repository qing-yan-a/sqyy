package com.sqyy.hospital.patientportal.controller;

import com.sqyy.hospital.auth.TokenService;
import com.sqyy.hospital.common.ApiResponse;
import com.sqyy.hospital.patientportal.service.CozeService;
import com.sqyy.hospital.patientportal.service.HealthNewsQueueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient-center")
public class CozeProxyController {

    private final CozeService cozeService;
    private final HealthNewsQueueService newsQueueService;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    public CozeProxyController(CozeService cozeService, HealthNewsQueueService newsQueueService, TokenService tokenService, ObjectMapper objectMapper) {
        this.cozeService = cozeService;
        this.newsQueueService = newsQueueService;
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
    }

    /**
     * AI 助手对话
     */
    @PostMapping("/ai/chat")
    public ApiResponse<?> aiChat(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String message = body.get("message");
        if (message == null || message.isBlank()) {
            return ApiResponse.error(400, "消息不能为空");
        }

        String userId = tokenService.parse(getToken(request)).getSubject();
        String reply = cozeService.chatWithAI(userId, message);
        return ApiResponse.success(Map.of("reply", reply));
    }

    /**
     * 健康资讯
     */
    /**
     * 健康资讯 - 返回结构化数组
     */
    @GetMapping("/health-news")
    public ApiResponse<?> healthNews(HttpServletRequest request) {
        String userId = tokenService.parse(getToken(request)).getSubject();
        String newsJson = newsQueueService.getNextMessage(userId);
        try {
            List<Map<String, String>> newsList = objectMapper.readValue(newsJson,
                    new TypeReference<List<Map<String, String>>>() {});
            return ApiResponse.success(newsList);
        } catch (Exception e) {
            return ApiResponse.error(500, "资讯格式异常");
        }
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return "";
    }
}
