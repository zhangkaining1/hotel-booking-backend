package com.hotel.service;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * 支付宝支付服务
 */
public interface AlipayService {

    /**
     * 创建支付宝电脑网站支付表单
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @return 支付宝支付表单HTML
     */
    String createPayForm(Long orderId, Long userId);

    /**
     * 处理支付宝异步通知
     *
     * @param params 通知参数
     * @return 处理结果（success 或 fail）
     */
    String handleNotify(Map<String, String> params);

    /**
     * 查询订单支付状态
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @return 是否已支付
     */
    boolean queryPayStatus(Long orderId, Long userId);

    /**
     * 订单退款
     *
     * @param paymentNo 支付流水号
     * @param amount    退款金额
     * @return 退款结果
     */
    boolean refund(String paymentNo, java.math.BigDecimal amount);
}
