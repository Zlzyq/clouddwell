package com.cloud.customer.service.impl;

import com.cloud.customer.entity.CustomerEntity;
import com.cloud.customer.mapper.CustomerMapper;
import com.cloud.customer.service.CustomerService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerMapper customerMapper;
    public CustomerServiceImpl(CustomerMapper customerMapper) { this.customerMapper = customerMapper; }
    @Override public List<CustomerEntity> list() { return customerMapper.selectAll(); }
    @Override public CustomerEntity getById(Long id) { return customerMapper.selectById(id); }
    @Override public CustomerEntity save(CustomerEntity entity) { return customerMapper.insert(entity); }
    @Override public CustomerEntity updateById(Long id, CustomerEntity entity) { return customerMapper.updateById(id, entity); }
    @Override public void deleteById(Long id) { customerMapper.deleteById(id); }
}
