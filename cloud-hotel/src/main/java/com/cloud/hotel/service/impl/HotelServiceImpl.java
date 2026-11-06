package com.cloud.hotel.service.impl;

import com.cloud.hotel.entity.HotelEntity;
import com.cloud.hotel.mapper.HotelMapper;
import com.cloud.hotel.service.HotelService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HotelServiceImpl implements HotelService {
    private final HotelMapper hotelMapper;
    public HotelServiceImpl(HotelMapper hotelMapper) { this.hotelMapper = hotelMapper; }
    @Override public List<HotelEntity> list() { return hotelMapper.selectAll(); }
    @Override public HotelEntity getById(Long id) { return hotelMapper.selectById(id); }
    @Override public HotelEntity save(HotelEntity entity) { return hotelMapper.insert(entity); }
    @Override public HotelEntity updateById(Long id, HotelEntity entity) { return hotelMapper.updateById(id, entity); }
    @Override public void deleteById(Long id) { hotelMapper.deleteById(id); }
}
