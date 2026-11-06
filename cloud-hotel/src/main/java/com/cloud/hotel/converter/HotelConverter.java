package com.cloud.hotel.converter;

import com.cloud.hotel.dto.HotelSaveDTO;
import com.cloud.hotel.entity.HotelEntity;
import com.cloud.hotel.vo.HotelVO;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class HotelConverter {
    public HotelVO toVO(HotelEntity entity) {
        if (entity == null) return null;
        HotelVO vo = new HotelVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setAddress(entity.getAddress());
        vo.setPhone(entity.getPhone());
        vo.setStarRating(entity.getStarRating());
        vo.setDescription(entity.getDescription());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
    public List<HotelVO> toVO(List<HotelEntity> entities) {
        return entities.stream().map(this::toVO).toList();
    }
    public HotelEntity toEntity(HotelSaveDTO dto) {
        if (dto == null) return null;
        HotelEntity entity = new HotelEntity();
        entity.setName(dto.getName());
        entity.setAddress(dto.getAddress());
        entity.setPhone(dto.getPhone());
        entity.setStarRating(dto.getStarRating());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}
