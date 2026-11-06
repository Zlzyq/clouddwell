package com.cloud.room.converter;

import com.cloud.room.dto.RoomSaveDTO;
import com.cloud.room.entity.RoomEntity;
import com.cloud.room.vo.RoomVO;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class RoomConverter {
    public RoomVO toVO(RoomEntity entity) {
        if (entity == null) return null;
        RoomVO vo = new RoomVO();
        vo.setId(entity.getId());
        vo.setRoomNumber(entity.getRoomNumber());
        vo.setType(entity.getType());
        vo.setPrice(entity.getPrice());
        vo.setCapacity(entity.getCapacity());
        vo.setStatus(entity.getStatus());
        vo.setHotelId(entity.getHotelId());
        vo.setDescription(entity.getDescription());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
    public List<RoomVO> toVO(List<RoomEntity> entities) {
        return entities.stream().map(this::toVO).toList();
    }
    public RoomEntity toEntity(RoomSaveDTO dto) {
        if (dto == null) return null;
        RoomEntity entity = new RoomEntity();
        entity.setRoomNumber(dto.getRoomNumber());
        entity.setType(dto.getType());
        entity.setPrice(dto.getPrice());
        entity.setCapacity(dto.getCapacity());
        entity.setStatus(dto.getStatus());
        entity.setHotelId(dto.getHotelId());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
