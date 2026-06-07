package com.hotel.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.result.Result;
import com.hotel.model.dto.GuestRoomSaveDTO;
import com.hotel.model.vo.GuestRoomVO;
import com.hotel.model.vo.PageVO;
import com.hotel.service.GuestRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/guest-rooms")
public class GuestRoomAdminController {

    @Autowired
    private GuestRoomService guestRoomService;

    @GetMapping
    public Result<PageVO<GuestRoomVO>> listGuestRooms(
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) Long roomTypeId,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<GuestRoomVO> page = guestRoomService.getGuestRoomPage(hotelId, roomTypeId, roomNumber, pageNum, pageSize);
        return Result.success(new PageVO<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @PostMapping
    public Result<Void> saveOrUpdate(@RequestBody GuestRoomSaveDTO dto) {
        guestRoomService.saveOrUpdateGuestRoom(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        guestRoomService.deleteGuestRoom(id);
        return Result.success();
    }

    @GetMapping("/available")
    public Result<List<GuestRoomVO>> getAvailableRooms(
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) Long roomTypeId) {
        List<GuestRoomVO> rooms = guestRoomService.getAvailableRooms(hotelId, roomTypeId);
        return Result.success(rooms);
    }
}
