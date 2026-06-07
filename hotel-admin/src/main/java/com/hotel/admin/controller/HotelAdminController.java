package com.hotel.admin.controller;

import com.hotel.common.result.Result;
import com.hotel.model.entity.Hotel;
import com.hotel.model.vo.AdminHotelVO;
import com.hotel.model.vo.PageVO;
import com.hotel.service.HotelService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/hotels")
public class HotelAdminController {

    private final HotelService hotelService;

    public HotelAdminController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public Result<PageVO<AdminHotelVO>> listHotels(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer star,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageVO<AdminHotelVO> page = hotelService.adminListHotels(city, keyword, star, pageNum, pageSize);
        return Result.success(page);
    }

    @PostMapping
    public Result<Void> addHotel(@RequestBody Hotel hotel) {
        hotelService.addHotel(hotel);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateHotel(@RequestBody Hotel hotel) {
        hotelService.updateHotel(hotel);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        hotelService.changeHotelStatus(id, status);
        return Result.success();
    }
}
