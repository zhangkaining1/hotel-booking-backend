package com.hotel.admin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.common.result.Result;
import com.hotel.model.entity.SysAdmin;
import com.hotel.service.SysAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    @Autowired
    private SysAdminService sysAdminService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        // 查询数据库中的管理员信息
        SysAdmin admin = sysAdminService.getOne(
                new LambdaQueryWrapper<SysAdmin>()
                        .eq(SysAdmin::getUsername, username)
        );

        if (admin != null && password.equals(admin.getPassword())) {
            if (admin.getStatus() != null && admin.getStatus() == 0) {
                return Result.fail(403, "该账号已被禁用");
            }
            
            StpUtil.login(admin.getId());
            Map<String, Object> data = new HashMap<>();
            data.put("token", StpUtil.getTokenValue());
            
            Map<String, Object> adminInfo = new HashMap<>();
            adminInfo.put("username", admin.getUsername());
            adminInfo.put("hotelId", admin.getHotelId());
            adminInfo.put("realName", admin.getRealName());
            
            data.put("admin", adminInfo);
            return Result.success(data);
        } else {
            return Result.fail(401, "用户名或密码错误");
        }
    }
    
    @PostMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return Result.success(null);
    }
}
