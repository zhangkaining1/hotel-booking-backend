package com.hotel.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.mapper.FacilityMapper;
import com.hotel.model.entity.Facility;
import com.hotel.service.FacilityService;
import org.springframework.stereotype.Service;

@Service
public class FacilityServiceImpl extends ServiceImpl<FacilityMapper, Facility> implements FacilityService {
}
