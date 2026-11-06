package com.cloud.room.service;

import com.cloud.room.entity.RoomEntity;
import java.util.List;

public interface RoomService {
    List<RoomEntity> list();
    RoomEntity getById(Long id);
    RoomEntity save(RoomEntity entity);
    RoomEntity updateById(Long id, RoomEntity entity);
    void deleteById(Long id);
}
