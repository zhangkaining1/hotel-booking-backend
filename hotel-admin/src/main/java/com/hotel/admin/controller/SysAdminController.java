package com.hotel.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.result.Result;
import com.hotel.model.entity.SysAdmin;
import com.hotel.model.vo.PageVO;
import com.hotel.service.SysAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/system/admins")
public class SysAdminController {

    @Autowired
    private SysAdminService sysAdminService;

    @GetMapping
    public Result<PageVO<SysAdmin>> listAdmins(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<SysAdmin> page = sysAdminService.getAdminPage(keyword, pageNum, pageSize);
        return Result.success(new PageVO<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @PostMapping
    public Result<Void> saveAdmin(@RequestBody SysAdmin sysAdmin) {
        // If it's a new admin, and password is not provided, maybe set a default one? Or require it.
        if (sysAdmin.getId() == null && (sysAdmin.getPassword() == null || sysAdmin.getPassword().isEmpty())) {
            sysAdmin.setPassword("123456"); // Default password, normally should be encrypted
        }
        sysAdminService.saveOrUpdate(sysAdmin);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateAdmin(@RequestBody SysAdmin sysAdmin) {
        sysAdminService.saveOrUpdate(sysAdmin);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAdmin(@PathVariable Long id) {
        sysAdminService.removeById(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysAdmin admin = new SysAdmin();
        admin.setId(id);
        admin.setStatus(status);
        sysAdminService.updateById(admin);
        return Result.success();
    }
}
