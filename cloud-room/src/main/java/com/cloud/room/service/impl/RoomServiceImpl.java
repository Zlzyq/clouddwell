package com.cloud.room.service.impl;

import com.cloud.room.entity.RoomEntity;
import com.cloud.room.mapper.RoomMapper;
import com.cloud.room.service.RoomService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    private final RoomMapper roomMapper;
    public RoomServiceImpl(RoomMapper roomMapper) { this.roomMapper = roomMapper; }
    @Override public List<RoomEntity> list() { return roomMapper.selectAll(); }
    @Override public RoomEntity getById(Long id) { return roomMapper.selectById(id); }
    @Override public RoomEntity save(RoomEntity entity) { return roomMapper.insert(entity); }
    @Override public RoomEntity updateById(Long id, RoomEntity entity) { return roomMapper.updateById(id, entity); }
    @Override public void deleteById(Long id) { roomMapper.deleteById(id); }
}
