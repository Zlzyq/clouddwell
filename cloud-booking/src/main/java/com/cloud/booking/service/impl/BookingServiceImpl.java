package com.cloud.booking.service.impl;

import com.cloud.booking.entity.BookingEntity;
import com.cloud.booking.mapper.BookingMapper;
import com.cloud.booking.service.BookingService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    public BookingServiceImpl(BookingMapper bookingMapper) { this.bookingMapper = bookingMapper; }
    @Override public List<BookingEntity> list() { return bookingMapper.selectAll(); }
    @Override public BookingEntity getById(Long id) { return bookingMapper.selectById(id); }
    @Override public BookingEntity save(BookingEntity entity) { return bookingMapper.insert(entity); }
    @Override public BookingEntity updateById(Long id, BookingEntity entity) { return bookingMapper.updateById(id, entity); }
    @Override public void deleteById(Long id) { bookingMapper.deleteById(id); }
}
