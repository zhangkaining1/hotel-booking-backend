package com.hotel.admin.controller;

import com.hotel.common.result.Result;
import com.hotel.model.vo.PageVO;
import com.hotel.service.data.DemoDataStore;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;
import com.hotel.service.RoomCalendarService;
import com.hotel.model.vo.RoomCalendarVO;
import com.hotel.model.dto.RoomCalendarSaveDTO;

@RestController
@RequestMapping("/api/admin/room")
public class RoomAdminController {

    private final DemoDataStore demoDataStore;
    private final RoomCalendarService roomCalendarService;

    public RoomAdminController(DemoDataStore demoDataStore, RoomCalendarService roomCalendarService) {
        this.demoDataStore = demoDataStore;
        this.roomCalendarService = roomCalendarService;
    }

    @GetMapping("/categories")
    public Result<PageVO<DemoDataStore.DemoRoomCategory>> listCategories(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(demoDataStore.adminListCategories(pageNum, pageSize));
    }

    @PostMapping("/categories")
    public Result<Void> addCategory(@RequestBody DemoDataStore.DemoRoomCategory category) {
        demoDataStore.adminAddCategory(category);
        return Result.success(null);
    }

    @PutMapping("/categories/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody DemoDataStore.DemoRoomCategory category) {
        demoDataStore.adminUpdateCategory(id, category);
        return Result.success(null);
    }

    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        demoDataStore.adminDeleteCategory(id);
        return Result.success(null);
    }

    @GetMapping("/types")
    public Result<PageVO<DemoDataStore.DemoRoomType>> listRoomTypes(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(demoDataStore.adminListRoomTypes(pageNum, pageSize));
    }

    @PostMapping("/types")
    public Result<Void> addRoomType(@RequestBody DemoDataStore.DemoRoomType roomType) {
        demoDataStore.adminAddRoomType(roomType);
        return Result.success(null);
    }

    @PutMapping("/types/{id}")
    public Result<Void> updateRoomType(@PathVariable Long id, @RequestBody DemoDataStore.DemoRoomType roomType) {
        demoDataStore.adminUpdateRoomType(id, roomType);
        return Result.success(null);
    }

    @DeleteMapping("/types/{id}")
    public Result<Void> deleteRoomType(@PathVariable Long id) {
        demoDataStore.adminDeleteRoomType(id);
        return Result.success(null);
    }

    @GetMapping("/calendar")
    public Result<List<RoomCalendarVO>> getCalendar(
            @RequestParam Long roomTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(roomCalendarService.getCalendar(roomTypeId, startDate, endDate));
    }

    @PostMapping("/calendar")
    public Result<Void> saveCalendar(@RequestBody RoomCalendarSaveDTO dto) {
        roomCalendarService.saveCalendar(dto);
        return Result.success();
    }
}
