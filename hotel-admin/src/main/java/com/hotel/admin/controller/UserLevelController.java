package com.hotel.admin.controller;

import com.hotel.common.result.Result;
import com.hotel.model.entity.UserLevel;
import com.hotel.service.UserLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/user-levels")
public class UserLevelController {

    @Autowired
    private UserLevelService userLevelService;

    @GetMapping
    public Result<List<UserLevel>> listAllLevels() {
        return Result.success(userLevelService.getAllLevels());
    }

    @PostMapping
    public Result<Void> saveOrUpdateLevel(@RequestBody UserLevel level) {
        userLevelService.saveOrUpdateLevel(level);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteLevel(@PathVariable Long id) {
        userLevelService.deleteLevel(id);
        return Result.success();
    }
}
