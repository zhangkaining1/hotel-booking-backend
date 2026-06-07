package com.hotel.api.controller;

import com.hotel.api.support.CurrentUserResolver;
import com.hotel.common.result.Result;
import com.hotel.model.dto.AiChatMessageDTO;
import com.hotel.service.AiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;
    private final CurrentUserResolver currentUserResolver;

    public AiController(AiService aiService, CurrentUserResolver currentUserResolver) {
        this.aiService = aiService;
        this.currentUserResolver = currentUserResolver;
    }

    @PostMapping("/chat")
    public Result<String> chat(@Validated @RequestBody ChatRequest request, HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolveUserId(httpRequest);
        String response = aiService.chat(userId, request.getMessage(), request.getMessages());
        return Result.success(response);
    }

    @PostMapping("/customer-service")
    public Result<String> customerService(@Validated @RequestBody ChatRequest request, HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolveUserId(httpRequest);
        String response = aiService.chat(userId, request.getMessage(), request.getMessages());
        return Result.success(response);
    }

    @GetMapping("/review-summary/{hotelId}")
    public Result<String> reviewSummary(@PathVariable("hotelId") Long hotelId) {
        String response = aiService.summarizeReviews(hotelId);
        return Result.success(response);
    }

    @Data
    public static class ChatRequest {
        @NotBlank(message = "请输入咨询内容")
        @Size(max = 1000, message = "单次咨询内容不能超过1000字")
        private String message;

        @Size(max = 12, message = "上下文消息过多")
        private List<AiChatMessageDTO> messages;
    }
}
