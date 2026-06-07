package com.hotel.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.result.Result;
import com.hotel.model.entity.SysUser;
import com.hotel.model.vo.PageVO;
import com.hotel.service.SysUserService;
import com.hotel.service.UserPointsLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    @Autowired
    private SysUserService sysUserService;
    
    @Autowired
    private UserPointsLogService userPointsLogService;

    @GetMapping
    public Result<PageVO<SysUser>> listUsers(
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<SysUser> page = sysUserService.getUserPage(nickname, phone, email, status, pageNum, pageSize);
        // Clear passwords before returning to frontend
        page.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(new PageVO<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<SysUser> getUserDetail(@PathVariable("id") Long userId) {
        SysUser user = sysUserService.getById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PostMapping
    public Result<Void> createUser(@RequestBody SysUser sysUser) {
        if (sysUser.getPassword() == null || sysUser.getPassword().isEmpty()) {
            sysUser.setPassword("123456");
        }
        sysUserService.save(sysUser);
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable("id") Long userId, @RequestBody SysUser sysUser) {
        sysUser.setId(userId);
        sysUser.setPassword(null); // Do not update password through this endpoint
        sysUserService.updateById(sysUser);
        return Result.success(null);
    }

    @PostMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable("id") Long userId, @RequestParam("status") Integer status) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setStatus(status);
        sysUserService.updateById(user);
        return Result.success(null);
    }

    @PostMapping("/{id}/password/reset")
    public Result<Void> resetPassword(@PathVariable("id") Long userId) {
        sysUserService.resetPassword(userId, "123456");
        return Result.success(null);
    }

    @PostMapping("/{id}/points")
    public Result<Void> rechargePoints(@PathVariable("id") Long userId, @RequestBody Map<String, Object> params) {
        Integer points = (Integer) params.get("points");
        String remark = (String) params.get("remark");
        if (points == null || points == 0) {
            return Result.fail("积分不能为0");
        }
        userPointsLogService.addPoints(userId, points, "admin_recharge", remark != null ? remark : "后台手动调整", null);
        return Result.success(null);
    }
}
