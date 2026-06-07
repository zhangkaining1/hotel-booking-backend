package com.hotel.admin.controller;
import com.hotel.common.result.Result;
import com.hotel.model.entity.Country;
import com.hotel.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/countrys")
public class CountryController {
    @Autowired
    private CountryService service;
    
    @GetMapping
    public Result<List<Country>> list() {
        return Result.success(service.list());
    }
    
    @PostMapping
    public Result<Void> add(@RequestBody Country entity) {
        service.save(entity);
        return Result.success();
    }
    
    @PutMapping
    public Result<Void> update(@RequestBody Country entity) {
        service.updateById(entity);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.removeById(id);
        return Result.success();
    }
}
