package com.cloud.booking.service;

import com.cloud.booking.entity.BookingEntity;
import java.util.List;

public interface BookingService {
    List<BookingEntity> list();
    BookingEntity getById(Long id);
    BookingEntity save(BookingEntity entity);
    BookingEntity updateById(Long id, BookingEntity entity);
    void deleteById(Long id);
}
