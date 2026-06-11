package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.mapper.RoomInventoryCalendarMapper;
import com.hotel.mapper.RoomPriceCalendarMapper;
import com.hotel.model.dto.RoomCalendarSaveDTO;
import com.hotel.model.entity.RoomInventoryCalendar;
import com.hotel.model.entity.RoomPriceCalendar;
import com.hotel.model.vo.RoomCalendarVO;
import com.hotel.service.RoomCalendarService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoomCalendarServiceImpl implements RoomCalendarService {

    private final RoomPriceCalendarMapper priceMapper;
    private final RoomInventoryCalendarMapper inventoryMapper;

    public RoomCalendarServiceImpl(RoomPriceCalendarMapper priceMapper, RoomInventoryCalendarMapper inventoryMapper) {
        this.priceMapper = priceMapper;
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    public List<RoomCalendarVO> getCalendar(Long roomTypeId, LocalDate startDate, LocalDate endDate) {
        List<RoomPriceCalendar> prices = priceMapper.selectList(new LambdaQueryWrapper<RoomPriceCalendar>()
                .eq(RoomPriceCalendar::getRoomTypeId, roomTypeId)
                .ge(RoomPriceCalendar::getDate, startDate)
                .le(RoomPriceCalendar::getDate, endDate));
        List<RoomInventoryCalendar> inventories = inventoryMapper.selectList(new LambdaQueryWrapper<RoomInventoryCalendar>()
                .eq(RoomInventoryCalendar::getRoomTypeId, roomTypeId)
                .ge(RoomInventoryCalendar::getDate, startDate)
                .le(RoomInventoryCalendar::getDate, endDate));

        Map<LocalDate, RoomPriceCalendar> priceMap = prices.stream().collect(Collectors.toMap(RoomPriceCalendar::getDate, p -> p));
        Map<LocalDate, RoomInventoryCalendar> invMap = inventories.stream().collect(Collectors.toMap(RoomInventoryCalendar::getDate, i -> i));

        List<RoomCalendarVO> list = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            RoomCalendarVO vo = new RoomCalendarVO();
            vo.setDate(date);
            RoomPriceCalendar p = priceMap.get(date);
            if (p != null) {
                vo.setPrice(p.getPrice());
            }
            RoomInventoryCalendar i = invMap.get(date);
            if (i != null) {
                vo.setTotalInventory(i.getTotalInventory());
                vo.setUsedInventory(i.getUsedInventory());
                vo.setLockedInventory(i.getLockedInventory());
            } else {
                vo.setTotalInventory(0);
                vo.setUsedInventory(0);
                vo.setLockedInventory(0);
            }
            list.add(vo);
        }
        return list;
    }

    @Override
    @Transactional
    public void saveCalendar(RoomCalendarSaveDTO dto) {
        for (LocalDate date = dto.getStartDate(); !date.isAfter(dto.getEndDate()); date = date.plusDays(1)) {
            if (dto.getPrice() != null) {
                RoomPriceCalendar p = priceMapper.selectOne(new LambdaQueryWrapper<RoomPriceCalendar>()
                        .eq(RoomPriceCalendar::getRoomTypeId, dto.getRoomTypeId())
                        .eq(RoomPriceCalendar::getDate, date));
                if (p == null) {
                    p = new RoomPriceCalendar();
                    p.setRoomTypeId(dto.getRoomTypeId());
                    p.setDate(date);
                    p.setPrice(dto.getPrice());
                    priceMapper.insert(p);
                } else {
                    p.setPrice(dto.getPrice());
                    priceMapper.updateById(p);
                }
            }

            if (dto.getInventory() != null) {
                RoomInventoryCalendar i = inventoryMapper.selectOne(new LambdaQueryWrapper<RoomInventoryCalendar>()
                        .eq(RoomInventoryCalendar::getRoomTypeId, dto.getRoomTypeId())
                        .eq(RoomInventoryCalendar::getDate, date));
                if (i == null) {
                    i = new RoomInventoryCalendar();
                    i.setRoomTypeId(dto.getRoomTypeId());
                    i.setDate(date);
                    i.setTotalInventory(dto.getInventory());
                    i.setUsedInventory(0);
                    i.setLockedInventory(0);
                    inventoryMapper.insert(i);
                } else {
                    i.setTotalInventory(dto.getInventory());
                    inventoryMapper.updateById(i);
                }
            }
        }
    }
}
