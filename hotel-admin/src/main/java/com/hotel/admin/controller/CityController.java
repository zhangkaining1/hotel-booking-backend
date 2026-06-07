package com.hotel.admin.controller;
import com.hotel.common.result.Result;
import com.hotel.model.entity.City;
import com.hotel.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/citys")
public class CityController {
    @Autowired
    private CityService service;
    
    @GetMapping
    public Result<List<City>> list() {
        return Result.success(service.list());
    }
    
    @PostMapping
    public Result<Void> add(@RequestBody City entity) {
        service.save(entity);
        return Result.success();
    }
    
    @PutMapping
    public Result<Void> update(@RequestBody City entity) {
        service.updateById(entity);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.removeById(id);
        return Result.success();
    }
}
