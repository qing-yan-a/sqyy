package com.sqyy.hospital.ai;

import com.sqyy.hospital.auth.TokenService;
import com.sqyy.hospital.common.ApiResponse;
import com.sqyy.hospital.service.HospitalPersistenceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final AiChatService aiChatService;
    private final HospitalPersistenceService persistenceService;
    private final TokenService tokenService;

    // 每个用户的对话历史（最多保留最近 20 条）
    private final Map<String, List<AiChatService.ChatMessage>> chatHistories = new ConcurrentHashMap<>();
    private static final int MAX_HISTORY = 20;

    public AiChatController(AiChatService aiChatService, HospitalPersistenceService persistenceService, TokenService tokenService) {
        this.aiChatService = aiChatService;
        this.persistenceService = persistenceService;
        this.tokenService = tokenService;
    }

    @PostMapping("/chat")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','RECEPTION')")
    public ApiResponse<Map<String, String>> chat(@RequestBody @Valid ChatRequest request, HttpServletRequest httpRequest) {
        String token = getToken(httpRequest);
        String userId = tokenService.parse(token).getSubject();
        List<AiChatService.ChatMessage> history = chatHistories.computeIfAbsent(userId, k -> new ArrayList<>());

        String systemPrompt = buildSystemPrompt();
        String reply = aiChatService.chat(systemPrompt, history, request.message());

        // 保存对话历史
        history.add(new AiChatService.ChatMessage("user", request.message()));
        history.add(new AiChatService.ChatMessage("assistant", reply));
        // 超出限制时裁剪旧消息
        while (history.size() > MAX_HISTORY) {
            history.remove(0);
        }

        return ApiResponse.success(Map.of("reply", reply));
    }

    private String buildSystemPrompt() {
        return """
                你是一个社区医院的智能医疗助手，服务于医生和医院工作人员。
                你的职责包括：
                - 回答医学知识、疾病症状、治疗方案等问题
                - 协助医生进行诊断参考（仅供参考，不替代医生判断）
                - 解答药品用法、用量、注意事项
                - 提供健康管理建议
                
                回答要求：
                - 使用中文回答
                - 语言专业但易懂
                - 涉及用药建议时提醒用户以医生处方为准
                - 紧急情况建议患者立即就医
                - 不确定的问题坦诚说明
                """;
    }

    public record ChatRequest(
            @NotBlank(message = "消息不能为空") String message
    ) {}

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return "";
    }
}
