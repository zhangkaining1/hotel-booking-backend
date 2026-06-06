package com.hotel.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI 对话记录表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_chat_record")
public class AiChatRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @TableField("user_id")
    private Long userId;

    /** 请求内容 */
    @TableField("request_content")
    private String requestContent;

    /** 响应内容 */
    @TableField("response_content")
    private String responseContent;

    /** 耗时（毫秒） */
    @TableField("cost_ms")
    private Integer costMs;
}
