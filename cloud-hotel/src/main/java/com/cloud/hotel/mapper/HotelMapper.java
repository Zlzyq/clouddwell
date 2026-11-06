package com.cloud.hotel.mapper;

import com.cloud.common.persistence.AbstractJsonFileMapper;
import com.cloud.common.properties.DemoDataProperties;
import com.cloud.common.util.DateUtils;
import com.cloud.hotel.entity.HotelEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Repository
public class HotelMapper extends AbstractJsonFileMapper<HotelEntity> {
    public HotelMapper(DemoDataProperties properties) throws IOException {
        super(
                Path.of(properties.getDataDir()).toAbsolutePath().normalize().resolve("hotels.json"),
                new TypeReference<List<HotelEntity>>() { },
                "demo-seed/hotels.json",
                entity -> { if (isBlank(entity.getCreateTime())) entity.setCreateTime(DateUtils.formatNow()); });
    }
}
