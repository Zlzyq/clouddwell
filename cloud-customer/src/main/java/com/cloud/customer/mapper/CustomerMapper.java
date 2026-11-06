package com.cloud.customer.mapper;

import com.cloud.common.persistence.AbstractJsonFileMapper;
import com.cloud.common.properties.DemoDataProperties;
import com.cloud.common.util.DateUtils;
import com.cloud.customer.entity.CustomerEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Repository
public class CustomerMapper extends AbstractJsonFileMapper<CustomerEntity> {
    public CustomerMapper(DemoDataProperties properties) throws IOException {
        super(
                Path.of(properties.getDataDir()).toAbsolutePath().normalize().resolve("customers.json"),
                new TypeReference<List<CustomerEntity>>() { },
                "demo-seed/customers.json",
                entity -> { if (isBlank(entity.getCreateTime())) entity.setCreateTime(DateUtils.formatNow()); });
    }
}
