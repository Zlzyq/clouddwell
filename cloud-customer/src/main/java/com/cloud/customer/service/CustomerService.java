package com.cloud.customer.service;

import com.cloud.customer.entity.CustomerEntity;
import java.util.List;

public interface CustomerService {
    List<CustomerEntity> list();
    CustomerEntity getById(Long id);
    CustomerEntity save(CustomerEntity entity);
    CustomerEntity updateById(Long id, CustomerEntity entity);
    void deleteById(Long id);
}
