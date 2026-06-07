package com.hotel.admin.controller;
import com.hotel.common.result.Result;
import com.hotel.model.entity.Facility;
import com.hotel.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/facilitys")
public class FacilityController {
    @Autowired
    private FacilityService service;
    
    @GetMapping
    public Result<List<Facility>> list() {
        return Result.success(service.list());
    }
    
    @PostMapping
    public Result<Void> add(@RequestBody Facility entity) {
        service.save(entity);
        return Result.success();
    }
    
    @PutMapping
    public Result<Void> update(@RequestBody Facility entity) {
        service.updateById(entity);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.removeById(id);
        return Result.success();
    }
}
