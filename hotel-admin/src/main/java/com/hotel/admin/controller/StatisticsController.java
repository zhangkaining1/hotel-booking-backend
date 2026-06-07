package com.hotel.admin.controller;

import com.hotel.common.result.Result;
import com.hotel.model.vo.DashboardVO;
import com.hotel.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboardData(@RequestParam(required = false) Long hotelId) {
        return Result.success(statisticsService.getDashboardData(hotelId));
    }
}
