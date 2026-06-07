package com.hotel.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.result.Result;
import com.hotel.model.dto.CheckinRequestDTO;
import com.hotel.model.vo.CheckinRecordVO;
import com.hotel.model.vo.PageVO;
import com.hotel.service.CheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/checkins")
public class CheckinAdminController {

    @Autowired
    private CheckinService checkinService;

    @GetMapping
    public Result<PageVO<CheckinRecordVO>> listCheckins(
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String guestName,
            @RequestParam(required = false) String idCard,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<CheckinRecordVO> page = checkinService.getCheckinPage(hotelId, orderNo, guestName, idCard, pageNum, pageSize);
        return Result.success(new PageVO<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @PostMapping
    public Result<Void> checkin(@RequestBody CheckinRequestDTO dto) {
        checkinService.checkin(dto);
        return Result.success();
    }

    @PostMapping("/{id}/checkout")
    public Result<Void> checkout(@PathVariable Long id) {
        checkinService.checkout(id);
        return Result.success();
    }
}
