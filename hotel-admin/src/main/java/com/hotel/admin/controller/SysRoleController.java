package com.hotel.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.result.Result;
import com.hotel.model.dto.SysRoleDTO;
import com.hotel.model.entity.SysRole;
import com.hotel.model.vo.PageVO;
import com.hotel.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/system/roles")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping
    public Result<PageVO<SysRole>> listRoles(
            @RequestParam(required = false) String roleName,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<SysRole> page = sysRoleService.getRolePage(roleName, pageNum, pageSize);
        return Result.success(new PageVO<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @PostMapping
    public Result<Void> saveOrUpdateRole(@RequestBody SysRoleDTO dto) {
        sysRoleService.saveOrUpdateRole(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        sysRoleService.deleteRole(id);
        return Result.success();
    }

    @GetMapping("/{id}/permissions")
    public Result<List<Long>> getRolePermissions(@PathVariable Long id) {
        return Result.success(sysRoleService.getRolePermissionIds(id));
    }

    @PostMapping("/{id}/permissions")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody SysRoleDTO dto) {
        sysRoleService.assignPermissions(id, dto.getPermissionIds());
        return Result.success();
    }
}
