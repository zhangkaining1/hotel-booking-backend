package com.hotel.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.common.enums.OrderStatus;
import com.hotel.common.exception.BusinessException;
import com.hotel.common.result.ResultCode;
import com.hotel.mapper.HotelOrderMapper;
import com.hotel.mapper.PaymentRecordMapper;
import com.hotel.mapper.RoomInventoryCalendarMapper;
import com.hotel.model.entity.HotelOrder;
import com.hotel.model.entity.PaymentRecord;
import com.hotel.model.entity.RoomInventoryCalendar;
import com.hotel.service.AlipayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
public class AlipayServiceImpl implements AlipayService {

    @Value("${alipay.app-id}")
    private String appId;

    @Value("${alipay.private-key}")
    private String privateKey;

    @Value("${alipay.public-key}")
    private String publicKey;

    @Value("${alipay.gateway-url}")
    private String gatewayUrl;

    @Value("${alipay.sign-type:RSA2}")
    private String signType;

    @Value("${alipay.charset:UTF-8}")
    private String charset;

    @Value("${alipay.format:json}")
    private String format;

    @Value("${alipay.return-url}")
    private String returnUrl;

    @Value("${alipay.notify-url}")
    private String notifyUrl;

    private final HotelOrderMapper hotelOrderMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final RoomInventoryCalendarMapper roomInventoryCalendarMapper;

    public AlipayServiceImpl(HotelOrderMapper hotelOrderMapper, 
                             PaymentRecordMapper paymentRecordMapper,
                             RoomInventoryCalendarMapper roomInventoryCalendarMapper) {
        this.hotelOrderMapper = hotelOrderMapper;
        this.paymentRecordMapper = paymentRecordMapper;
        this.roomInventoryCalendarMapper = roomInventoryCalendarMapper;
    }

    private AlipayClient getAlipayClient() {
        return new DefaultAlipayClient(gatewayUrl, appId, privateKey, format, charset, publicKey, signType);
    }

    @Override
    @Transactional
    public String createPayForm(Long orderId, Long userId) {
        HotelOrder order = hotelOrderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }

        // 自动检查是否超时
        if (order.getStatus() == OrderStatus.PENDING_PAYMENT.getCode()
                && order.getPayDeadline() != null
                && LocalDateTime.now().isAfter(order.getPayDeadline())) {
            order.setStatus(OrderStatus.CANCELLED.getCode());
            order.setCancelTime(LocalDateTime.now());
            hotelOrderMapper.updateById(order);
            releaseLockedInventory(order);
            throw new BusinessException(ResultCode.ORDER_EXPIRED);
        }

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT.getCode()) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }

        // 检查是否已存在支付记录
        PaymentRecord existing = paymentRecordMapper.selectByOrderId(orderId);
        if (existing != null && existing.getStatus() == 1) {
            throw new BusinessException(ResultCode.ORDER_ALREADY_PAID);
        }

        String paymentNo;
        if (existing != null) {
            paymentNo = existing.getPaymentNo();
        } else {
            paymentNo = "PAY" + System.currentTimeMillis();
            PaymentRecord record = new PaymentRecord();
            record.setPaymentNo(paymentNo);
            record.setOrderId(order.getId());
            record.setOrderNo(order.getOrderNo());
            record.setAmount(order.getPayAmount());
            record.setStatus(0);
            paymentRecordMapper.insert(record);
        }

        try {
            AlipayClient alipayClient = getAlipayClient();
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setNotifyUrl(notifyUrl);
            request.setReturnUrl(appendOrderId(returnUrl, order.getId()));

            String bizContent = "{" +
                    "\"out_trade_no\":\"" + paymentNo + "\"," +
                    "\"total_amount\":\"" + order.getPayAmount().toString() + "\"," +
                    "\"subject\":\"酒店订单 " + order.getOrderNo() + "\"," +
                    "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"" +
                    "}";
            request.setBizContent(bizContent);

            return alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            log.error("创建支付宝支付表单失败", e);
            throw new BusinessException(ResultCode.PAYMENT_FAILED);
        }
    }

    @Override
    @Transactional
    public String handleNotify(Map<String, String> params) {
        log.info("支付宝异步通知: {}", params);

        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(params, publicKey, charset, signType);
            if (!signVerified) {
                log.error("支付宝通知验签失败");
                return "fail";
            }

            String tradeStatus = params.get("trade_status");
            if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
                log.info("支付宝通知交易状态非成功: {}", tradeStatus);
                return "success";
            }

            String paymentNo = params.get("out_trade_no");
            String tradeNo = params.get("trade_no");
            String totalAmount = params.get("total_amount");

            PaymentRecord record = paymentRecordMapper.selectByPaymentNo(paymentNo);
            if (record == null) {
                log.error("支付记录不存在: {}", paymentNo);
                return "fail";
            }

            // 幂等处理：已处理过则直接返回成功
            if (record.getStatus() == 1) {
                return "success";
            }

            HotelOrder order = hotelOrderMapper.selectById(record.getOrderId());
            if (order == null) {
                log.error("订单不存在: {}", record.getOrderId());
                return "fail";
            }

            // 校验金额
            BigDecimal notifyAmount = new BigDecimal(totalAmount);
            if (notifyAmount.compareTo(record.getAmount()) != 0) {
                log.error("支付金额不匹配, 通知金额: {}, 记录金额: {}", totalAmount, record.getAmount());
                return "fail";
            }

            // 校验订单状态：已取消的订单不处理支付成功
            if (order.getStatus() == OrderStatus.CANCELLED.getCode()) {
                log.warn("订单已取消但仍收到支付通知, orderId: {}", order.getId());
                // 记录异常但不返回fail，避免支付宝重复通知
                record.setStatus(2); // 标记为失败
                record.setCallbackContent("订单已取消");
                paymentRecordMapper.updateById(record);
                return "success";
            }

            // 更新支付记录
            record.setTradeNo(tradeNo);
            record.setStatus(1);
            record.setPayTime(LocalDateTime.now());
            record.setCallbackContent(truncate(params.toString(), 2000));
            paymentRecordMapper.updateById(record);

            // 更新订单状态
            order.setStatus(OrderStatus.PAID.getCode());
            order.setPayTime(LocalDateTime.now());
            hotelOrderMapper.updateById(order);

            // 将锁定的库存转换为已使用库存
            convertLockedToUsedInventory(order);

            log.info("支付宝支付成功处理完成, orderId: {}, paymentNo: {}", order.getId(), paymentNo);
            return "success";

        } catch (Exception e) {
            log.error("处理支付宝通知异常", e);
            return "fail";
        }
    }

    @Override
    public boolean queryPayStatus(Long orderId, Long userId) {
        HotelOrder order = hotelOrderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT.getCode()) {
            return order.getStatus() == OrderStatus.PAID.getCode()
                    || order.getStatus() == OrderStatus.CONFIRMED.getCode()
                    || order.getStatus() == OrderStatus.CHECKED_IN.getCode()
                    || order.getStatus() == OrderStatus.COMPLETED.getCode();
        }

        PaymentRecord record = paymentRecordMapper.selectByOrderId(orderId);
        if (record == null) {
            return false;
        }

        if (record.getStatus() == 1) {
            if (order.getStatus() == OrderStatus.PENDING_PAYMENT.getCode()) {
                order.setStatus(OrderStatus.PAID.getCode());
                order.setPayTime(record.getPayTime() != null ? record.getPayTime() : LocalDateTime.now());
                hotelOrderMapper.updateById(order);
                convertLockedToUsedInventory(order);
            }
            return true;
        }

        try {
            AlipayClient alipayClient = getAlipayClient();
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            request.setBizContent("{\"out_trade_no\":\"" + record.getPaymentNo() + "\"}");
            AlipayTradeQueryResponse response = alipayClient.execute(request);

            if (response.isSuccess() && ("TRADE_SUCCESS".equals(response.getTradeStatus())
                    || "TRADE_FINISHED".equals(response.getTradeStatus()))) {
                // 主动查询到已支付，同步更新状态
                if (record.getStatus() != 1) {
                    record.setStatus(1);
                    record.setPayTime(LocalDateTime.now());
                    paymentRecordMapper.updateById(record);

                    order.setStatus(OrderStatus.PAID.getCode());
                    order.setPayTime(LocalDateTime.now());
                    hotelOrderMapper.updateById(order);
                    convertLockedToUsedInventory(order);
                }
                return true;
            }
        } catch (AlipayApiException e) {
            log.error("查询支付宝订单状态失败", e);
        }
        return false;
    }

    @Override
    public boolean refund(String paymentNo, BigDecimal amount) {
        try {
            AlipayClient alipayClient = getAlipayClient();
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            String bizContent = "{" +
                    "\"out_trade_no\":\"" + paymentNo + "\"," +
                    "\"refund_amount\":\"" + amount.toString() + "\"" +
                    "}";
            request.setBizContent(bizContent);
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                log.info("支付宝退款成功, paymentNo: {}, amount: {}", paymentNo, amount);
                return true;
            } else {
                log.error("支付宝退款失败, paymentNo: {}, code: {}, msg: {}, sub_code: {}, sub_msg: {}",
                        paymentNo, response.getCode(), response.getMsg(), response.getSubCode(), response.getSubMsg());
                return false;
            }
        } catch (AlipayApiException e) {
            log.error("支付宝退款异常, paymentNo: {}", paymentNo, e);
            return false;
        }
    }

    private void convertLockedToUsedInventory(HotelOrder order) {
        for (LocalDate date = order.getCheckinDate(); date.isBefore(order.getCheckoutDate()); date = date.plusDays(1)) {
            RoomInventoryCalendar i = roomInventoryCalendarMapper.selectOne(new LambdaQueryWrapper<RoomInventoryCalendar>()
                    .eq(RoomInventoryCalendar::getRoomTypeId, order.getRoomTypeId())
                    .eq(RoomInventoryCalendar::getDate, date));
            if (i != null) {
                i.setLockedInventory(Math.max(0, i.getLockedInventory() - order.getRoomCount()));
                i.setUsedInventory(i.getUsedInventory() + order.getRoomCount());
                roomInventoryCalendarMapper.updateById(i);
            }
        }
    }

    private void releaseLockedInventory(HotelOrder order) {
        for (LocalDate date = order.getCheckinDate(); date.isBefore(order.getCheckoutDate()); date = date.plusDays(1)) {
            RoomInventoryCalendar i = roomInventoryCalendarMapper.selectOne(new LambdaQueryWrapper<RoomInventoryCalendar>()
                    .eq(RoomInventoryCalendar::getRoomTypeId, order.getRoomTypeId())
                    .eq(RoomInventoryCalendar::getDate, date));
            if (i != null) {
                i.setLockedInventory(Math.max(0, i.getLockedInventory() - order.getRoomCount()));
                roomInventoryCalendarMapper.updateById(i);
            }
        }
    }

    private String appendOrderId(String url, Long orderId) {
        if (url == null || url.isBlank()) {
            return url;
        }
        String separator = url.contains("?") ? "&" : "?";
        return url + separator + "orderId=" + orderId;
    }

    private String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength);
    }
}
