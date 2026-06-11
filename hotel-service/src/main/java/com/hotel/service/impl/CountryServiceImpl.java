package com.hotel.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.mapper.CountryMapper;
import com.hotel.model.entity.Country;
import com.hotel.service.CountryService;
import org.springframework.stereotype.Service;

@Service
public class CountryServiceImpl extends ServiceImpl<CountryMapper, Country> implements CountryService {
}
