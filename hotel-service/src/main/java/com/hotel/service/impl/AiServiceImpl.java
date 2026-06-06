package com.hotel.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.mapper.AiChatRecordMapper;
import com.hotel.mapper.HotelMapper;
import com.hotel.mapper.ReviewMapper;
import com.hotel.model.entity.AiChatRecord;
import com.hotel.model.entity.Hotel;
import com.hotel.model.entity.Review;
import com.hotel.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AiServiceImpl implements AiService {

    @Value("${deepseek.api-key:}")
    private String apiKey;

    @Value("${deepseek.api-url:https://api.deepseek.com/chat/completions}")
    private String apiUrl;

    @Value("${deepseek.model:deepseek-chat}")
    private String model;

    private final AiChatRecordMapper aiChatRecordMapper;
    private final ReviewMapper reviewMapper;
    private final HotelMapper hotelMapper;

    public AiServiceImpl(AiChatRecordMapper aiChatRecordMapper, ReviewMapper reviewMapper, HotelMapper hotelMapper) {
        this.aiChatRecordMapper = aiChatRecordMapper;
        this.reviewMapper = reviewMapper;
        this.hotelMapper = hotelMapper;
    }

    @Override
    public String chat(Long userId, String message) {
        long start = System.currentTimeMillis();
        String systemPrompt = "你是一个酒店预订助手。你可以帮用户：\n" +
                "1. 根据预算、位置、人数推荐酒店\n" +
                "2. 解答订单、支付、退款、入住政策等问题\n" +
                "3. 提供行程建议和周边景点信息\n" +
                "4. 解释酒店设施和房型差异\n" +
                "请用中文回答，保持简洁友好。如果你需要推荐酒店，请明确询问用户的城市、预算、入住日期和人数。";

        String response = callDeepSeek(systemPrompt, message);

        long cost = System.currentTimeMillis() - start;
        saveRecord(userId, message, response, (int) cost);
        return response;
    }

    @Override
    public String summarizeReviews(Long hotelId) {
        Hotel hotel = hotelMapper.selectById(hotelId);
        if (hotel == null) {
            return "酒店不存在";
        }

        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getHotelId, hotelId)
                .eq(Review::getStatus, 1)
                .orderByDesc(Review::getCreateTime)
                .last("LIMIT 50");
        List<Review> reviews = reviewMapper.selectList(wrapper);

        if (reviews.isEmpty()) {
            return "该酒店暂无评价";
        }

        String reviewTexts = reviews.stream()
                .map(r -> "评分:" + r.getOverallScore() + ", 内容:" + (r.getContent() != null ? r.getContent() : ""))
                .collect(Collectors.joining("\n"));

        String systemPrompt = "你是一个酒店评价分析助手。请根据以下用户评价，总结该酒店的优点和缺点，给出3-5个关键洞察。请用中文回答，格式清晰。";
        String userMessage = "酒店名称：" + hotel.getNameCn() + "\n用户评价：\n" + reviewTexts;

        long start = System.currentTimeMillis();
        String response = callDeepSeek(systemPrompt, userMessage);
        long cost = System.currentTimeMillis() - start;
        saveRecord(null, "总结酒店评价: " + hotelId, response, (int) cost);
        return response;
    }

    private String callDeepSeek(String systemPrompt, String userMessage) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("DeepSeek API Key 未配置");
            return "AI 服务暂不可用，请稍后重试或联系管理员配置 API Key。";
        }

        JSONObject body = new JSONObject();
        body.set("model", model);
        body.set("stream", false);
        body.set("temperature", 0.7);

        JSONArray messages = new JSONArray();
        messages.add(new JSONObject().set("role", "system").set("content", systemPrompt));
        messages.add(new JSONObject().set("role", "user").set("content", userMessage));
        body.set("messages", messages);

        try (HttpResponse response = HttpRequest.post(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(body.toString())
                .timeout(60000)
                .execute()) {

            if (response.getStatus() != 200) {
                log.error("DeepSeek API 调用失败, status: {}, body: {}", response.getStatus(), response.body());
                return "AI 服务调用失败，请稍后重试。";
            }

            JSONObject result = JSONUtil.parseObj(response.body());
            JSONArray choices = result.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                return "AI 暂无回复，请稍后重试。";
            }
            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
            return message.getStr("content", "AI 暂无回复");

        } catch (Exception e) {
            log.error("DeepSeek API 调用异常", e);
            return "AI 服务异常，请稍后重试。";
        }
    }

    private void saveRecord(Long userId, String request, String response, int costMs) {
        try {
            AiChatRecord record = new AiChatRecord();
            record.setUserId(userId);
            record.setRequestContent(request);
            record.setResponseContent(response);
            record.setCostMs(costMs);
            aiChatRecordMapper.insert(record);
        } catch (Exception e) {
            log.error("保存 AI 对话记录失败", e);
        }
    }
}
