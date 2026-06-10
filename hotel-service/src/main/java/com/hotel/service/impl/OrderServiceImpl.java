package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.constants.AppConstants;
import com.hotel.common.enums.OrderStatus;
import com.hotel.common.exception.BusinessException;
import com.hotel.common.result.ResultCode;
import com.hotel.mapper.HotelMapper;
import com.hotel.mapper.HotelOrderMapper;
import com.hotel.mapper.RefundRecordMapper;
import com.hotel.mapper.ReviewMapper;
import com.hotel.mapper.RoomInventoryCalendarMapper;
import com.hotel.mapper.RoomPriceCalendarMapper;
import com.hotel.mapper.RoomTypeMapper;
import com.hotel.model.dto.CreateOrderRequest;
import com.hotel.model.entity.Hotel;
import com.hotel.model.entity.HotelOrder;
import com.hotel.model.entity.RefundRecord;
import com.hotel.model.entity.Review;
import com.hotel.model.entity.RoomInventoryCalendar;
import com.hotel.model.entity.RoomPriceCalendar;
import com.hotel.model.entity.RoomType;
import com.hotel.model.query.OrderListQuery;
import com.hotel.model.vo.OrderCreatedVO;
import com.hotel.model.vo.OrderDetailVO;
import com.hotel.model.vo.OrderStatusVO;
import com.hotel.model.vo.OrderSummaryVO;
import com.hotel.model.vo.PageVO;
import com.hotel.service.AlipayService;
import com.hotel.service.OrderService;
import com.hotel.service.SysUserService;
import com.hotel.service.UserLevelService;
import com.hotel.model.entity.SysUser;
import com.hotel.model.entity.UserLevel;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class OrderServiceImpl extends ServiceImpl<HotelOrderMapper, HotelOrder> implements OrderService {

    private final HotelOrderMapper hotelOrderMapper;
    private final HotelMapper hotelMapper;
    private final RoomTypeMapper roomTypeMapper;
    private final RoomPriceCalendarMapper roomPriceCalendarMapper;
    private final RoomInventoryCalendarMapper roomInventoryCalendarMapper;
    private final RefundRecordMapper refundRecordMapper;
    private final ReviewMapper reviewMapper;
    private final AlipayService alipayService;
    private final SysUserService sysUserService;
    private final UserLevelService userLevelService;

    public OrderServiceImpl(HotelOrderMapper hotelOrderMapper, 
                            HotelMapper hotelMapper, 
                            RoomTypeMapper roomTypeMapper,
                            RoomPriceCalendarMapper roomPriceCalendarMapper,
                            RoomInventoryCalendarMapper roomInventoryCalendarMapper,
                            RefundRecordMapper refundRecordMapper,
                            ReviewMapper reviewMapper,
                            SysUserService sysUserService,
                            UserLevelService userLevelService,
                            @Lazy AlipayService alipayService) {
        this.hotelOrderMapper = hotelOrderMapper;
        this.hotelMapper = hotelMapper;
        this.roomTypeMapper = roomTypeMapper;
        this.roomPriceCalendarMapper = roomPriceCalendarMapper;
        this.roomInventoryCalendarMapper = roomInventoryCalendarMapper;
        this.refundRecordMapper = refundRecordMapper;
        this.reviewMapper = reviewMapper;
        this.sysUserService = sysUserService;
        this.userLevelService = userLevelService;
        this.alipayService = alipayService;
    }

    @Override
    @Transactional
    public OrderCreatedVO createOrder(Long userId, CreateOrderRequest request) {
        Hotel hotel = hotelMapper.selectById(request.getHotelId());
        if (hotel == null) {
            throw new BusinessException(ResultCode.HOTEL_NOT_FOUND);
        }
        RoomType roomType = roomTypeMapper.selectById(request.getRoomTypeId());
        if (roomType == null) {
            throw new BusinessException(ResultCode.ROOM_TYPE_NOT_FOUND);
        }
        if (!roomType.getHotelId().equals(hotel.getId())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "Room type does not belong to hotel");
        }
        long nights = ChronoUnit.DAYS.between(request.getCheckinDate(), request.getCheckoutDate());
        if (nights <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "Checkout date must be after checkin date");
        }
        if (request.getRoomCount() <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "Room count must be greater than 0");
        }

        BigDecimal originalAmount = BigDecimal.ZERO;
        List<RoomInventoryCalendar> inventoriesToUpdate = new ArrayList<>();
        
        for (LocalDate date = request.getCheckinDate(); date.isBefore(request.getCheckoutDate()); date = date.plusDays(1)) {
            // Price
            RoomPriceCalendar p = roomPriceCalendarMapper.selectOne(new LambdaQueryWrapper<RoomPriceCalendar>()
                    .eq(RoomPriceCalendar::getRoomTypeId, roomType.getId())
                    .eq(RoomPriceCalendar::getDate, date));
            BigDecimal dayPrice = p != null && p.getPrice() != null ? p.getPrice() : roomType.getBasePrice();
            originalAmount = originalAmount.add(dayPrice.multiply(BigDecimal.valueOf(request.getRoomCount())));
            
            // Inventory
            RoomInventoryCalendar i = roomInventoryCalendarMapper.selectOne(new LambdaQueryWrapper<RoomInventoryCalendar>()
                    .eq(RoomInventoryCalendar::getRoomTypeId, roomType.getId())
                    .eq(RoomInventoryCalendar::getDate, date));
            if (i == null || (i.getTotalInventory() - i.getUsedInventory() - i.getLockedInventory() < request.getRoomCount())) {
                throw new BusinessException(ResultCode.ROOM_INVENTORY_NOT_ENOUGH.getCode(), "日期 " + date + " 库存不足");
            }
            i.setLockedInventory(i.getLockedInventory() + request.getRoomCount());
            inventoriesToUpdate.add(i);
        }
        
        for (RoomInventoryCalendar i : inventoriesToUpdate) {
            roomInventoryCalendarMapper.updateById(i);
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        
        // 计算会员折扣
        SysUser user = sysUserService.getById(userId);
        if (user != null && user.getLevelId() != null) {
            UserLevel userLevel = userLevelService.getById(user.getLevelId());
            if (userLevel != null && userLevel.getDiscountRate() != null) {
                // 折扣额 = 原始金额 * (1 - discountRate)
                // 或者说 应付 = 原始金额 * discountRate
                // 假设 discountRate 存储的是 0.9 (9折)
                BigDecimal discountRate = userLevel.getDiscountRate();
                BigDecimal payAmountExpected = originalAmount.multiply(discountRate);
                discountAmount = originalAmount.subtract(payAmountExpected);
            }
        }

        BigDecimal payAmount = originalAmount.subtract(discountAmount);

        HotelOrder order = new HotelOrder();
        order.setOrderNo("HB" + System.currentTimeMillis());
        order.setUserId(userId);
        order.setHotelId(hotel.getId());
        order.setRoomTypeId(roomType.getId());
        order.setCheckinDate(request.getCheckinDate());
        order.setCheckoutDate(request.getCheckoutDate());
        order.setNights((int) nights);
        order.setRoomCount(request.getRoomCount());
        order.setGuestName(request.getGuestName());
        order.setGuestPhone(request.getGuestPhone());
        order.setSpecialRequest(request.getSpecialRequest());
        order.setOriginalAmount(originalAmount.setScale(2, RoundingMode.HALF_UP));
        order.setDiscountAmount(discountAmount.setScale(2, RoundingMode.HALF_UP));
        order.setPayAmount(payAmount.setScale(2, RoundingMode.HALF_UP));
        order.setStatus(OrderStatus.PENDING_PAYMENT.getCode());
        order.setPayDeadline(LocalDateTime.now().plusMinutes(AppConstants.ORDER_PAY_TIMEOUT_MINUTES));
        hotelOrderMapper.insert(order);

        return new OrderCreatedVO(order.getId(), order.getOrderNo(), order.getPayDeadline());
    }

    @Override
    public PageVO<OrderSummaryVO> listOrders(Long userId, OrderListQuery query) {
        LambdaQueryWrapper<HotelOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HotelOrder::getUserId, userId);
        if (query.getStatus() != null) {
            wrapper.eq(HotelOrder::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(HotelOrder::getCreateTime);
        List<HotelOrder> orders = hotelOrderMapper.selectList(wrapper);

        List<OrderSummaryVO> list = orders.stream()
                .peek(this::autoCancelIfExpired)
                .map(this::toSummary)
                .collect(Collectors.toList());

        int pn = query.getPageNum() != null && query.getPageNum() > 0 ? query.getPageNum() : 1;
        int ps = query.getPageSize() != null && query.getPageSize() > 0
                ? Math.min(query.getPageSize(), AppConstants.MAX_PAGE_SIZE)
                : AppConstants.DEFAULT_PAGE_SIZE;
        int from = Math.min((pn - 1) * ps, list.size());
        int to = Math.min(from + ps, list.size());
        return new PageVO<>(list.subList(from, to), list.size(), pn, ps);
    }

    @Override
    public OrderDetailVO getOrderDetail(Long userId, Long orderId) {
        HotelOrder order = hotelOrderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        autoCancelIfExpired(order);
        return toDetail(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        HotelOrder order = hotelOrderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        autoCancelIfExpired(order);
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT.getCode()) {
            throw new BusinessException(ResultCode.ORDER_CANCEL_NOT_ALLOWED);
        }
        order.setStatus(OrderStatus.CANCELLED.getCode());
        order.setCancelTime(LocalDateTime.now());
        hotelOrderMapper.updateById(order);
        
        releaseLockedInventory(order);
    }

    @Override
    @Transactional
    public void autoCancelExpiredOrders() {
        LambdaQueryWrapper<HotelOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HotelOrder::getStatus, OrderStatus.PENDING_PAYMENT.getCode())
                .lt(HotelOrder::getPayDeadline, LocalDateTime.now());
        List<HotelOrder> expiredOrders = hotelOrderMapper.selectList(wrapper);
        for (HotelOrder order : expiredOrders) {
            order.setStatus(OrderStatus.CANCELLED.getCode());
            order.setCancelTime(LocalDateTime.now());
            hotelOrderMapper.updateById(order);
            releaseLockedInventory(order);
        }
    }

    @Override
    @Transactional
    public void applyRefund(Long userId, Long orderId, String reason) {
        HotelOrder order = hotelOrderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (order.getStatus() != OrderStatus.PAID.getCode()) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "只有已支付订单可以申请退款");
        }
        order.setStatus(OrderStatus.REFUND_APPLYING.getCode());
        hotelOrderMapper.updateById(order);
        
        RefundRecord record = new RefundRecord();
        record.setRefundNo("REF" + System.currentTimeMillis());
        record.setOrderId(order.getId());
        record.setOrderNo(order.getOrderNo());
        record.setRefundAmount(order.getPayAmount());
        record.setReason(reason);
        record.setAuditStatus(0); // 待审核
        refundRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void auditRefund(Long refundId, com.hotel.model.dto.AuditRefundRequest request) {
        RefundRecord record = refundRecordMapper.selectById(refundId);
        if (record == null || record.getAuditStatus() != 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "退款记录不存在或已处理");
        }

        HotelOrder order = hotelOrderMapper.selectById(record.getOrderId());
        
        if (request.getApprove()) {
            com.hotel.mapper.PaymentRecordMapper paymentRecordMapper = com.hotel.common.utils.SpringContextUtils.getBean(com.hotel.mapper.PaymentRecordMapper.class);
            com.hotel.model.entity.PaymentRecord paymentRecord = paymentRecordMapper.selectOne(new LambdaQueryWrapper<com.hotel.model.entity.PaymentRecord>().eq(com.hotel.model.entity.PaymentRecord::getOrderId, order.getId()));
            if (paymentRecord == null) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "支付记录不存在");
            }
            boolean success = alipayService.refund(paymentRecord.getPaymentNo(), record.getRefundAmount());
            if (success) {
                record.setAuditStatus(1);
                record.setRefundTime(LocalDateTime.now());
                refundRecordMapper.updateById(record);

                order.setStatus(OrderStatus.REFUNDED.getCode());
                hotelOrderMapper.updateById(order);

                // 释放已用库存
                for (LocalDate date = order.getCheckinDate(); date.isBefore(order.getCheckoutDate()); date = date.plusDays(1)) {
                    RoomInventoryCalendar i = roomInventoryCalendarMapper.selectOne(new LambdaQueryWrapper<RoomInventoryCalendar>()
                            .eq(RoomInventoryCalendar::getRoomTypeId, order.getRoomTypeId())
                            .eq(RoomInventoryCalendar::getDate, date));
                    if (i != null) {
                        i.setUsedInventory(Math.max(0, i.getUsedInventory() - order.getRoomCount()));
                        roomInventoryCalendarMapper.updateById(i);
                    }
                }
            } else {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "支付宝退款失败");
            }
        } else {
            record.setAuditStatus(2);
            refundRecordMapper.updateById(record);

            order.setStatus(OrderStatus.PAID.getCode()); // 恢复为已支付
            hotelOrderMapper.updateById(order);
        }
    }

    @Override
    public PageVO<OrderSummaryVO> adminListOrders(OrderListQuery query) {
        OrderListQuery safeQuery = query != null ? query : new OrderListQuery();
        int pn = safeQuery.getPageNum() != null && safeQuery.getPageNum() > 0 ? safeQuery.getPageNum() : 1;
        int ps = safeQuery.getPageSize() != null && safeQuery.getPageSize() > 0
                ? Math.min(safeQuery.getPageSize(), AppConstants.MAX_PAGE_SIZE)
                : AppConstants.DEFAULT_PAGE_SIZE;

        Page<HotelOrder> page = hotelOrderMapper.selectPage(new Page<>(pn, ps), buildAdminOrderWrapper(safeQuery));
        List<OrderSummaryVO> list = page.getRecords().stream()
                .peek(this::autoCancelIfExpired)
                .map(this::toSummary)
                .collect(Collectors.toList());
        return new PageVO<>(list, page.getTotal(), pn, ps);
    }

    @Override
    public List<OrderSummaryVO> adminListOrdersForExport(OrderListQuery query) {
        OrderListQuery safeQuery = query != null ? query : new OrderListQuery();
        return hotelOrderMapper.selectList(buildAdminOrderWrapper(safeQuery)).stream()
                .peek(this::autoCancelIfExpired)
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDetailVO adminGetOrderDetail(Long orderId) {
        HotelOrder order = hotelOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        autoCancelIfExpired(order);
        return toDetail(order);
    }

    @Override
    @Transactional
    public void adminConfirmOrder(Long orderId) {
        HotelOrder order = hotelOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        autoCancelIfExpired(order);
        if (order.getStatus() == OrderStatus.CONFIRMED.getCode()) {
            return;
        }
        if (order.getStatus() != OrderStatus.PAID.getCode()) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR.getCode(), "只有已支付待确认订单可以确认");
        }
        order.setStatus(OrderStatus.CONFIRMED.getCode());
        hotelOrderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void adminCancelOrder(Long orderId, String reason) {
        HotelOrder order = hotelOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        autoCancelIfExpired(order);
        Integer previousStatus = order.getStatus();
        if (previousStatus == OrderStatus.CANCELLED.getCode()) {
            return;
        }
        if (previousStatus == OrderStatus.CHECKED_IN.getCode()
                || previousStatus == OrderStatus.COMPLETED.getCode()
                || previousStatus == OrderStatus.REFUNDED.getCode()) {
            throw new BusinessException(ResultCode.ORDER_CANCEL_NOT_ALLOWED);
        }
        order.setStatus(OrderStatus.CANCELLED.getCode());
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(StringUtils.hasText(reason) ? reason : "管理员取消订单");
        hotelOrderMapper.updateById(order);

        if (previousStatus == OrderStatus.PENDING_PAYMENT.getCode()) {
            releaseLockedInventory(order);
        } else {
            releaseUsedInventory(order);
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

    private void releaseUsedInventory(HotelOrder order) {
        for (LocalDate date = order.getCheckinDate(); date.isBefore(order.getCheckoutDate()); date = date.plusDays(1)) {
            RoomInventoryCalendar i = roomInventoryCalendarMapper.selectOne(new LambdaQueryWrapper<RoomInventoryCalendar>()
                    .eq(RoomInventoryCalendar::getRoomTypeId, order.getRoomTypeId())
                    .eq(RoomInventoryCalendar::getDate, date));
            if (i != null) {
                int used = i.getUsedInventory() != null ? i.getUsedInventory() : 0;
                int count = order.getRoomCount() != null ? order.getRoomCount() : 0;
                i.setUsedInventory(Math.max(0, used - count));
                roomInventoryCalendarMapper.updateById(i);
            }
        }
    }

    private LambdaQueryWrapper<HotelOrder> buildAdminOrderWrapper(OrderListQuery query) {
        LambdaQueryWrapper<HotelOrder> wrapper = new LambdaQueryWrapper<>();
        if (query.getStatus() != null) {
            wrapper.eq(HotelOrder::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getOrderNo())) {
            wrapper.like(HotelOrder::getOrderNo, query.getOrderNo());
        }
        if (StringUtils.hasText(query.getGuestName())) {
            wrapper.and(w -> w.like(HotelOrder::getGuestName, query.getGuestName())
                    .or()
                    .like(HotelOrder::getGuestPhone, query.getGuestName()));
        }
        if (query.getStartDate() != null) {
            wrapper.ge(HotelOrder::getCreateTime, query.getStartDate().atStartOfDay());
        }
        if (query.getEndDate() != null) {
            wrapper.lt(HotelOrder::getCreateTime, query.getEndDate().plusDays(1).atStartOfDay());
        }
        wrapper.orderByDesc(HotelOrder::getCreateTime);
        return wrapper;
    }

    private void autoCancelIfExpired(HotelOrder order) {
        if (order.getStatus() == OrderStatus.PENDING_PAYMENT.getCode()
                && order.getPayDeadline() != null
                && LocalDateTime.now().isAfter(order.getPayDeadline())) {
            order.setStatus(OrderStatus.CANCELLED.getCode());
            order.setCancelTime(LocalDateTime.now());
            hotelOrderMapper.updateById(order);
            releaseLockedInventory(order);
        }
    }

    private OrderSummaryVO toSummary(HotelOrder order) {
        Hotel hotel = hotelMapper.selectById(order.getHotelId());
        RoomType roomType = roomTypeMapper.selectById(order.getRoomTypeId());
        OrderSummaryVO vo = new OrderSummaryVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setHotelId(order.getHotelId());
        vo.setHotelName(hotel != null ? hotel.getNameCn() : null);
        vo.setHotelCover(null); // 简化
        vo.setRoomTypeId(order.getRoomTypeId());
        vo.setRoomTypeName(roomType != null ? roomType.getName() : null);
        vo.setCheckinDate(order.getCheckinDate());
        vo.setCheckoutDate(order.getCheckoutDate());
        vo.setNights(order.getNights());
        vo.setRoomCount(order.getRoomCount());
        vo.setGuestName(order.getGuestName());
        vo.setGuestPhone(order.getGuestPhone());
        vo.setPayAmount(order.getPayAmount());
        vo.setRefundId(findLatestRefundId(order.getId()));
        vo.setStatus(new OrderStatusVO(order.getStatus(), OrderStatus.fromCode(order.getStatus()).getDescription()));
        vo.setPayDeadline(order.getPayDeadline());
        vo.setCreateTime(order.getCreateTime());
        return vo;
    }

    private OrderDetailVO toDetail(HotelOrder order) {
        Hotel hotel = hotelMapper.selectById(order.getHotelId());
        RoomType roomType = roomTypeMapper.selectById(order.getRoomTypeId());
        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setHotelId(order.getHotelId());
        vo.setHotelName(hotel != null ? hotel.getNameCn() : null);
        vo.setHotelAddress(hotel != null ? hotel.getAddress() : null);
        vo.setHotelCover(null);
        vo.setRoomTypeId(order.getRoomTypeId());
        vo.setRoomTypeName(roomType != null ? roomType.getName() : null);
        vo.setBedType(roomType != null ? String.valueOf(roomType.getBedTypeId()) : null);
        vo.setBreakfast(roomType != null ? String.valueOf(roomType.getBreakfastTypeId()) : null);
        vo.setCheckinDate(order.getCheckinDate());
        vo.setCheckoutDate(order.getCheckoutDate());
        vo.setNights(order.getNights());
        vo.setRoomCount(order.getRoomCount());
        vo.setGuestName(order.getGuestName());
        vo.setGuestPhone(order.getGuestPhone());
        vo.setSpecialRequest(order.getSpecialRequest());
        vo.setOriginalAmount(order.getOriginalAmount());
        vo.setDiscountAmount(order.getDiscountAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setRefundId(findLatestRefundId(order.getId()));
        vo.setStatus(new OrderStatusVO(order.getStatus(), OrderStatus.fromCode(order.getStatus()).getDescription()));
        vo.setPayDeadline(order.getPayDeadline());
        vo.setPayTime(order.getPayTime());
        vo.setCancelTime(order.getCancelTime());
        vo.setCreateTime(order.getCreateTime());
        vo.setIsReviewed(hasReviewed(order));
        return vo;
    }

    private Long findLatestRefundId(Long orderId) {
        if (orderId == null) {
            return null;
        }
        RefundRecord record = refundRecordMapper.selectOne(new LambdaQueryWrapper<RefundRecord>()
                .eq(RefundRecord::getOrderId, orderId)
                .orderByDesc(RefundRecord::getCreateTime)
                .last("LIMIT 1"));
        return record != null ? record.getId() : null;
    }

    private boolean hasReviewed(HotelOrder order) {
        Long count = reviewMapper.selectCount(new LambdaQueryWrapper<Review>()
                .eq(Review::getOrderId, order.getId())
                .eq(Review::getUserId, order.getUserId())
                .eq(Review::getDeleted, 0));
        return count != null && count > 0;
    }
}
