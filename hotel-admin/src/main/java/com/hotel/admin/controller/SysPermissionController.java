package com.hotel.admin.controller;

import com.hotel.common.result.Result;
import com.hotel.model.entity.SysPermission;
import com.hotel.model.vo.SysPermissionVO;
import com.hotel.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/system/permissions")
public class SysPermissionController {

    @Autowired
    private SysPermissionService sysPermissionService;

    @GetMapping("/tree")
    public Result<List<SysPermissionVO>> getPermissionTree() {
        return Result.success(sysPermissionService.getPermissionTree());
    }

    @PostMapping
    public Result<Void> saveOrUpdatePermission(@RequestBody SysPermission permission) {
        sysPermissionService.saveOrUpdatePermission(permission);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePermission(@PathVariable Long id) {
        sysPermissionService.deletePermission(id);
        return Result.success();
    }
}
