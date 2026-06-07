package com.hotel.admin.controller;

import com.hotel.common.result.Result;
import com.hotel.model.vo.PageVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.hotel.service.OrderService;
import com.hotel.model.dto.AuditRefundRequest;
import com.hotel.model.query.OrderListQuery;
import com.hotel.model.vo.OrderSummaryVO;
import com.hotel.model.vo.OrderDetailVO;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
public class OrderAdminController {

    private final OrderService orderService;

    public OrderAdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Result<PageVO<OrderSummaryVO>> listOrders(OrderListQuery query) {
        return Result.success(orderService.adminListOrders(query));
    }

    @GetMapping("/export")
    public void exportOrders(OrderListQuery query, HttpServletResponse response) throws IOException {
        List<OrderSummaryVO> orders = orderService.adminListOrdersForExport(query);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"orders.csv\"");

        StringBuilder csv = new StringBuilder();
        csv.append("订单号,酒店,房型,入住日期,离店日期,入住人,手机号,金额,状态,创建时间\n");
        for (OrderSummaryVO order : orders) {
            csv.append(csv(order.getOrderNo())).append(',')
                    .append(csv(order.getHotelName())).append(',')
                    .append(csv(order.getRoomTypeName())).append(',')
                    .append(csv(order.getCheckinDate())).append(',')
                    .append(csv(order.getCheckoutDate())).append(',')
                    .append(csv(order.getGuestName())).append(',')
                    .append(csv(order.getGuestPhone())).append(',')
                    .append(csv(amount(order.getPayAmount()))).append(',')
                    .append(csv(order.getStatus() != null ? order.getStatus().getName() : null)).append(',')
                    .append(csv(order.getCreateTime()))
                    .append('\n');
        }

        byte[] bom = new byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        response.getOutputStream().write(bom);
        response.getOutputStream().write(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/{id}")
    public Result<OrderDetailVO> getOrderDetail(@PathVariable("id") Long id) {
        return Result.success(orderService.adminGetOrderDetail(id));
    }

    @PostMapping("/{id}/confirm")
    public Result<Void> confirmOrder(@PathVariable("id") Long id) {
        orderService.adminConfirmOrder(id);
        return Result.success();
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable("id") Long id,
                                    @RequestBody(required = false) Map<String, String> payload) {
        String reason = payload != null ? payload.get("reason") : null;
        orderService.adminCancelOrder(id, reason);
        return Result.success();
    }

    @PostMapping("/refunds/{id}/audit")
    public Result<Void> auditRefund(@PathVariable("id") Long refundId, @RequestBody AuditRefundRequest request) {
        orderService.auditRefund(refundId, request);
        return Result.success();
    }

    private String amount(BigDecimal amount) {
        return amount == null ? "" : amount.toPlainString();
    }

    private String csv(Object value) {
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value);
        if (text.contains("\"") || text.contains(",") || text.contains("\n") || text.contains("\r")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }
}
