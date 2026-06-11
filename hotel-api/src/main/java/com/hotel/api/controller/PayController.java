package com.hotel.api.controller;

import com.hotel.api.support.CurrentUserResolver;
import com.hotel.common.result.Result;
import com.hotel.service.AlipayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/pay")
public class PayController {

    private final AlipayService alipayService;
    private final CurrentUserResolver currentUserResolver;

    public PayController(AlipayService alipayService, CurrentUserResolver currentUserResolver) {
        this.alipayService = alipayService;
        this.currentUserResolver = currentUserResolver;
    }

    /**
     * 创建支付宝支付（返回支付表单HTML）
     */
    @PostMapping("/alipay/create")
    public Result<String> createAlipay(@RequestParam("orderId") Long orderId,
                                       HttpServletRequest request) {
        Long userId = currentUserResolver.requireUserId(request);
        String form = alipayService.createPayForm(orderId, userId);
        return Result.success(form);
    }

    /**
     * 支付宝同步回调（页面跳转）
     */
    @GetMapping("/alipay/return")
    public Result<Map<String, Object>> alipayReturn(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put("outTradeNo", request.getParameter("out_trade_no"));
        result.put("tradeNo", request.getParameter("trade_no"));
        result.put("totalAmount", request.getParameter("total_amount"));
        result.put("success", "TRADE_SUCCESS".equals(request.getParameter("trade_status"))
                || request.getParameter("trade_status") == null); // 同步回调可能无trade_status
        return Result.success(result);
    }

    /**
     * 支付宝异步通知
     */
    @PostMapping("/alipay/notify")
    public String alipayNotify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            StringBuilder valueStr = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                valueStr.append(i == values.length - 1 ? values[i] : values[i] + ",");
            }
            params.put(name, valueStr.toString());
        }
        return alipayService.handleNotify(params);
    }

    /**
     * 查询订单支付状态
     */
    @GetMapping("/alipay/status")
    public Result<Boolean> queryPayStatus(@RequestParam("orderId") Long orderId,
                                          HttpServletRequest request) {
        Long userId = currentUserResolver.requireUserId(request);
        boolean paid = alipayService.queryPayStatus(orderId, userId);
        return Result.success(paid);
    }
}
