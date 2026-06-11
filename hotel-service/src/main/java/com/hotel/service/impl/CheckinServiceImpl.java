package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.exception.BusinessException;
import com.hotel.common.result.ResultCode;
import com.hotel.mapper.CheckinRecordMapper;
import com.hotel.model.dto.CheckinRequestDTO;
import com.hotel.model.entity.CheckinRecord;
import com.hotel.model.entity.GuestRoom;
import com.hotel.model.entity.HotelOrder;
import com.hotel.model.entity.RoomType;
import com.hotel.model.vo.CheckinRecordVO;
import com.hotel.service.CheckinService;
import com.hotel.service.GuestRoomService;
import com.hotel.service.OrderService;
import com.hotel.service.RoomTypeService;
import com.hotel.service.UserPointsLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CheckinServiceImpl extends ServiceImpl<CheckinRecordMapper, CheckinRecord> implements CheckinService {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private GuestRoomService guestRoomService;

    @Autowired
    private RoomTypeService roomTypeService;
    
    @Autowired
    private UserPointsLogService userPointsLogService;

    @Override
    public Page<CheckinRecordVO> getCheckinPage(Long hotelId, String orderNo, String guestName, String idCard, Integer current, Integer size) {
        LambdaQueryWrapper<CheckinRecord> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(guestName)) {
            wrapper.like(CheckinRecord::getGuestName, guestName);
        }
        if (StringUtils.hasText(idCard)) {
            wrapper.eq(CheckinRecord::getIdCard, idCard);
        }
        
        // 简单处理：如果按 orderNo 查询，先查询 order 表获取 orderId
        if (StringUtils.hasText(orderNo) || hotelId != null) {
            LambdaQueryWrapper<HotelOrder> orderWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.hasText(orderNo)) {
                orderWrapper.eq(HotelOrder::getOrderNo, orderNo);
            }
            if (hotelId != null) {
                orderWrapper.eq(HotelOrder::getHotelId, hotelId);
            }
            List<HotelOrder> orders = orderService.list(orderWrapper);
            if (orders.isEmpty()) {
                return new Page<>(current, size); // 没有匹配订单，直接返回空
            }
            List<Long> orderIds = orders.stream().map(HotelOrder::getId).collect(Collectors.toList());
            wrapper.in(CheckinRecord::getOrderId, orderIds);
        }
        
        wrapper.orderByDesc(CheckinRecord::getCreateTime);

        Page<CheckinRecord> page = new Page<>(current, size);
        this.page(page, wrapper);

        // 获取关联数据
        List<CheckinRecordVO> voList = new ArrayList<>();
        if (page.getRecords() != null && !page.getRecords().isEmpty()) {
            List<Long> orderIds = page.getRecords().stream()
                    .map(CheckinRecord::getOrderId)
                    .filter(java.util.Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
                    
            List<Long> roomIds = page.getRecords().stream()
                    .map(CheckinRecord::getGuestRoomId)
                    .filter(java.util.Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            Map<Long, HotelOrder> orderMap = new java.util.HashMap<>();
            if (!orderIds.isEmpty()) {
                List<HotelOrder> orderList = orderService.listByIds(orderIds);
                if (orderList != null) {
                    orderMap = orderList.stream().collect(Collectors.toMap(HotelOrder::getId, o -> o, (v1, v2) -> v1));
                }
            }
            
            Map<Long, GuestRoom> roomMap = new java.util.HashMap<>();
            if (!roomIds.isEmpty()) {
                List<GuestRoom> roomList = guestRoomService.listByIds(roomIds);
                if (roomList != null) {
                    roomMap = roomList.stream().collect(Collectors.toMap(GuestRoom::getId, r -> r, (v1, v2) -> v1));
                }
            }
            
            // 房型名称
            List<Long> roomTypeIds = orderMap.values().stream()
                    .map(HotelOrder::getRoomTypeId)
                    .filter(java.util.Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
                    
            Map<Long, String> roomTypeNameMap = new java.util.HashMap<>();
            if (!roomTypeIds.isEmpty()) {
                List<RoomType> typeList = roomTypeService.listByIds(roomTypeIds);
                if (typeList != null) {
                    roomTypeNameMap = typeList.stream().collect(Collectors.toMap(
                            RoomType::getId, 
                            r -> r.getName() != null ? r.getName() : "未知房型", 
                            (v1, v2) -> v1
                    ));
                }
            }

            for (CheckinRecord record : page.getRecords()) {
                CheckinRecordVO vo = new CheckinRecordVO();
                BeanUtils.copyProperties(record, vo);
                vo.setStatus(parseStatus(record.getStatus()));
                
                if (record.getOrderId() != null) {
                    HotelOrder order = orderMap.get(record.getOrderId());
                    if (order != null) {
                        vo.setOrderNo(order.getOrderNo());
                        if (order.getRoomTypeId() != null) {
                            vo.setRoomType(roomTypeNameMap.getOrDefault(order.getRoomTypeId(), "未知房型"));
                        } else {
                            vo.setRoomType("未知房型");
                        }
                    }
                }
                
                if (record.getGuestRoomId() != null) {
                    GuestRoom room = roomMap.get(record.getGuestRoomId());
                    if (room != null) {
                        vo.setRoomNo(room.getRoomNumber());
                    }
                }
                
                voList.add(vo);
            }
        }

        Page<CheckinRecordVO> voPage = new Page<>(current, size);
        voPage.setRecords(voList);
        voPage.setTotal(page.getTotal());
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkin(CheckinRequestDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getOrderNo())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "订单编号不能为空");
        }
        if (dto.getGuestRoomId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "请选择入住客房");
        }

        // 1. 查找订单
        HotelOrder order = orderService.getOne(new LambdaQueryWrapper<HotelOrder>().eq(HotelOrder::getOrderNo, dto.getOrderNo()));
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (order.getStatus() != 1 && order.getStatus() != 2) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR.getCode(), "订单状态不允许办理入住 (当前状态码:" + order.getStatus() + ")");
        }

        long activeCheckinCount = this.count(new LambdaQueryWrapper<CheckinRecord>()
                .eq(CheckinRecord::getOrderId, order.getId())
                .eq(CheckinRecord::getStatus, 1));
        if (activeCheckinCount > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该订单已办理入住");
        }

        // 2. 查找房间并锁定
        GuestRoom room = guestRoomService.getById(dto.getGuestRoomId());
        if (room == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "所选客房不存在");
        }
        if (room.getStatus() != 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该客房当前不处于空闲状态，无法分配");
        }
        if (order.getHotelId() != null && room.getHotelId() != null && !order.getHotelId().equals(room.getHotelId())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "所选客房不属于订单酒店");
        }
        if (order.getRoomTypeId() != null && room.getRoomTypeId() != null && !order.getRoomTypeId().equals(room.getRoomTypeId())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "所选客房房型与订单房型不一致");
        }

        // 3. 生成入住记录
        CheckinRecord record = new CheckinRecord();
        record.setOrderId(order.getId());
        record.setGuestRoomId(room.getId());
        record.setGuestName(order.getGuestName());
        record.setGuestPhone(order.getGuestPhone());
        record.setIdCard(dto.getIdCard());
        record.setCheckinTime(LocalDateTime.now());
        record.setStatus(1); // 1-已入住
        this.save(record);

        // 4. 更新订单和房间状态
        order.setStatus(4); // 4-已入住
        orderService.updateById(order);

        room.setStatus(2); // 2-已入住
        guestRoomService.updateById(room);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkout(Long checkinId) {
        CheckinRecord record = this.getById(checkinId);
        if (record == null) {
            throw new RuntimeException("入住记录不存在");
        }
        if (record.getStatus() != 1) {
            throw new RuntimeException("非入驻状态，无法办理退房");
        }

        // 1. 更新入住记录
        record.setCheckoutTime(LocalDateTime.now());
        record.setStatus(2); // 2-已离店
        this.updateById(record);

        // 2. 更新订单状态
        HotelOrder order = orderService.getById(record.getOrderId());
        if (order != null && order.getStatus() == 4) {
            order.setStatus(5); // 5-已完成
            orderService.updateById(order);
            
            // 订单完成后，根据实付金额发放等值积分 (1元=1积分)
            if (order.getPayAmount() != null) {
                int points = order.getPayAmount().intValue();
                if (points > 0) {
                    userPointsLogService.addPoints(
                        order.getUserId(), 
                        points, 
                        "order", 
                        "订单完成奖励积分", 
                        order.getId()
                    );
                }
            }
        }

        // 3. 释放客房
        GuestRoom room = guestRoomService.getById(record.getGuestRoomId());
        if (room != null && room.getStatus() == 2) {
            room.setStatus(0); // 0-空闲
            guestRoomService.updateById(room);
        }
    }

    private String parseStatus(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待入住";
            case 1: return "已入住";
            case 2: return "已离店";
            case 3: return "异常";
            default: return "未知";
        }
    }
}
