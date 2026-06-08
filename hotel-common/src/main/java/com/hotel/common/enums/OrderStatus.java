package com.hotel.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态枚举
 */
@Getter
@AllArgsConstructor
public enum OrderStatus {

    PENDING_PAYMENT(0, "待支付"),
    PAID(1, "已支付"),
    CONFIRMED(2, "已确认"),
    CANCELLED(3, "已取消"),
    CHECKED_IN(4, "已入住"),
    COMPLETED(5, "已完成"),
    REFUND_APPLYING(6, "退款申请中"),
    REFUNDED(7, "已退款"),
    REFUND_REJECTED(8, "退款拒绝");

    private final int code;
    private final String description;

    public static OrderStatus fromCode(int code) {
        for (OrderStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的订单状态: " + code);
    }
}
