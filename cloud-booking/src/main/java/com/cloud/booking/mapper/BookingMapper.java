package com.cloud.booking.mapper;

import com.cloud.common.persistence.AbstractJsonFileMapper;
import com.cloud.common.properties.DemoDataProperties;
import com.cloud.common.util.DateUtils;
import com.cloud.booking.entity.BookingEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Repository
public class BookingMapper extends AbstractJsonFileMapper<BookingEntity> {
    public BookingMapper(DemoDataProperties properties) throws IOException {
        super(
                Path.of(properties.getDataDir()).toAbsolutePath().normalize().resolve("bookings.json"),
                new TypeReference<List<BookingEntity>>() { },
                "demo-seed/bookings.json",
                entity -> { if (isBlank(entity.getCreateTime())) entity.setCreateTime(DateUtils.formatNow()); });
    }
}
