package com.hotel.api.controller;

import com.hotel.api.support.CurrentUserResolver;
import com.hotel.common.result.Result;
import com.hotel.model.dto.CreateOrderRequest;
import com.hotel.model.query.OrderListQuery;
import com.hotel.model.vo.OrderCreatedVO;
import com.hotel.model.vo.OrderDetailVO;
import com.hotel.model.vo.OrderSummaryVO;
import com.hotel.model.vo.PageVO;
import com.hotel.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.Data;

@Validated
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final CurrentUserResolver currentUserResolver;

    public OrderController(OrderService orderService, CurrentUserResolver currentUserResolver) {
        this.orderService = orderService;
        this.currentUserResolver = currentUserResolver;
    }

    @PostMapping
    public Result<OrderCreatedVO> createOrder(@RequestBody @Valid CreateOrderRequest request,
                                              HttpServletRequest httpServletRequest) {
        Long userId = currentUserResolver.requireUserId(httpServletRequest);
        return Result.success(orderService.createOrder(userId, request));
    }

    @GetMapping
    public Result<PageVO<OrderSummaryVO>> listOrders(OrderListQuery query, HttpServletRequest request) {
        Long userId = currentUserResolver.requireUserId(request);
        return Result.success(orderService.listOrders(userId, query));
    }

    @GetMapping("/{id}")
    public Result<OrderDetailVO> orderDetail(@PathVariable("id") Long orderId, HttpServletRequest request) {
        Long userId = currentUserResolver.requireUserId(request);
        return Result.success(orderService.getOrderDetail(userId, orderId));
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable("id") Long orderId, HttpServletRequest request) {
        Long userId = currentUserResolver.requireUserId(request);
        orderService.cancelOrder(userId, orderId);
        return Result.success();
    }

    @Data
    public static class RefundRequest {
        private String reason;
    }

    @PostMapping("/{id}/refund")
    public Result<Void> applyRefund(@PathVariable("id") Long orderId, @RequestBody RefundRequest payload, HttpServletRequest request) {
        Long userId = currentUserResolver.requireUserId(request);
        orderService.applyRefund(userId, orderId, payload.getReason());
        return Result.success();
    }
}
