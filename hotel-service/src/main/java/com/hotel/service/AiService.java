package com.hotel.service;

/**
 * AI 服务
 */
public interface AiService {

    /**
     * 通用 AI 对话
     *
     * @param userId  用户ID（可为null）
     * @param message 用户输入
     * @return AI 回复内容
     */
    String chat(Long userId, String message);

    /**
     * 酒店评价总结
     *
     * @param hotelId 酒店ID
     * @return AI 总结内容
     */
    String summarizeReviews(Long hotelId);
}
