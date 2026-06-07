package com.hotel.admin.controller;
import com.hotel.common.result.Result;
import com.hotel.model.entity.SystemConfig;
import com.hotel.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/system_configs")
public class SystemConfigController {
    @Autowired
    private SystemConfigService service;
    
    @GetMapping
    public Result<List<SystemConfig>> list() {
        return Result.success(service.list());
    }
    
    @PostMapping
    public Result<Void> add(@RequestBody SystemConfig entity) {
        service.save(entity);
        return Result.success();
    }
    
    @PutMapping
    public Result<Void> update(@RequestBody SystemConfig entity) {
        service.updateById(entity);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.removeById(id);
        return Result.success();
    }
}
