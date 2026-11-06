package com.cloud.booking.converter;

import com.cloud.booking.dto.BookingSaveDTO;
import com.cloud.booking.entity.BookingEntity;
import com.cloud.booking.vo.BookingVO;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class BookingConverter {
    public BookingVO toVO(BookingEntity entity) {
        if (entity == null) return null;
        BookingVO vo = new BookingVO();
        vo.setId(entity.getId());
        vo.setHotelId(entity.getHotelId());
        vo.setRoomId(entity.getRoomId());
        vo.setCustomerName(entity.getCustomerName());
        vo.setCustomerPhone(entity.getCustomerPhone());
        vo.setCheckInDate(entity.getCheckInDate());
        vo.setCheckOutDate(entity.getCheckOutDate());
        vo.setTotalAmount(entity.getTotalAmount());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
    public List<BookingVO> toVO(List<BookingEntity> entities) {
        return entities.stream().map(this::toVO).toList();
    }
    public BookingEntity toEntity(BookingSaveDTO dto) {
        if (dto == null) return null;
        BookingEntity entity = new BookingEntity();
        entity.setHotelId(dto.getHotelId());
        entity.setRoomId(dto.getRoomId());
        entity.setCustomerName(dto.getCustomerName());
        entity.setCustomerPhone(dto.getCustomerPhone());
        entity.setCheckInDate(dto.getCheckInDate());
        entity.setCheckOutDate(dto.getCheckOutDate());
        entity.setTotalAmount(dto.getTotalAmount());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}
