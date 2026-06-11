package com.hotel.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payment_record")
public class PaymentRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 支付流水号 */
    @TableField("payment_no")
    private String paymentNo;

    /** 订单ID */
    @TableField("order_id")
    private Long orderId;

    /** 订单编号 */
    @TableField("order_no")
    private String orderNo;

    /** 支付宝交易号 */
    @TableField("trade_no")
    private String tradeNo;

    /** 支付金额 */
    private BigDecimal amount;

    /** 支付状态 0-待支付 1-成功 2-失败 3-已退款 */
    private Integer status;

    /** 支付成功时间 */
    @TableField("pay_time")
    private LocalDateTime payTime;

    /** 回调内容摘要 */
    @TableField("callback_content")
    private String callbackContent;
}
