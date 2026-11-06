package com.cloud.hotel.service;

import com.cloud.hotel.entity.HotelEntity;
import java.util.List;

public interface HotelService {
    List<HotelEntity> list();
    HotelEntity getById(Long id);
    HotelEntity save(HotelEntity entity);
    HotelEntity updateById(Long id, HotelEntity entity);
    void deleteById(Long id);
}
