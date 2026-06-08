package com.hotel.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一响应状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /* ========== 通用状态码 ========== */
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    /* ========== 参数相关 4xx ========== */
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有操作权限"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方式不允许"),

    /* ========== 业务相关 5xx ========== */
    USER_NOT_FOUND(5001, "用户不存在"),
    USER_ALREADY_EXISTS(5002, "用户已存在"),
    PASSWORD_ERROR(5003, "密码错误"),
    VERIFY_CODE_ERROR(5004, "验证码错误"),
    VERIFY_CODE_EXPIRED(5005, "验证码已过期"),

    HOTEL_NOT_FOUND(5101, "酒店不存在"),
    ROOM_TYPE_NOT_FOUND(5102, "房型不存在"),
    ROOM_SOLD_OUT(5103, "房间已售罄"),
    ROOM_INVENTORY_NOT_ENOUGH(5104, "库存不足"),

    ORDER_NOT_FOUND(5201, "订单不存在"),
    ORDER_STATUS_ERROR(5202, "订单状态异常"),
    ORDER_EXPIRED(5203, "订单已过期"),
    ORDER_ALREADY_PAID(5204, "订单已支付"),
    ORDER_CANCEL_NOT_ALLOWED(5205, "订单不允许取消"),

    PAYMENT_FAILED(5301, "支付失败"),
    PAYMENT_VERIFY_FAILED(5302, "支付验签失败"),
    REFUND_FAILED(5303, "退款失败"),
    REFUND_NOT_ALLOWED(5304, "不允许退款"),

    FILE_UPLOAD_ERROR(5401, "文件上传失败"),
    FILE_TYPE_NOT_ALLOWED(5402, "文件类型不允许"),
    FILE_SIZE_EXCEEDED(5403, "文件大小超出限制");

    /** 状态码 */
    private final int code;

    /** 提示信息 */
    private final String message;
}
