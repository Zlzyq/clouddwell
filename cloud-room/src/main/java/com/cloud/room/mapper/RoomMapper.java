package com.cloud.room.mapper;

import com.cloud.common.persistence.AbstractJsonFileMapper;
import com.cloud.common.properties.DemoDataProperties;
import com.cloud.common.util.DateUtils;
import com.cloud.room.entity.RoomEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Repository
public class RoomMapper extends AbstractJsonFileMapper<RoomEntity> {
    public RoomMapper(DemoDataProperties properties) throws IOException {
        super(
                Path.of(properties.getDataDir()).toAbsolutePath().normalize().resolve("rooms.json"),
                new TypeReference<List<RoomEntity>>() { },
                "demo-seed/rooms.json",
                entity -> { if (isBlank(entity.getCreateTime())) entity.setCreateTime(DateUtils.formatNow()); });
    }
}
