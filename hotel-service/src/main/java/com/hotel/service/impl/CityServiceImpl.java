package com.hotel.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.mapper.CityMapper;
import com.hotel.model.entity.City;
import com.hotel.service.CityService;
import org.springframework.stereotype.Service;

@Service
public class CityServiceImpl extends ServiceImpl<CityMapper, City> implements CityService {
}
