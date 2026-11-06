package com.cloud.customer.converter;

import com.cloud.customer.dto.CustomerSaveDTO;
import com.cloud.customer.entity.CustomerEntity;
import com.cloud.customer.vo.CustomerVO;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CustomerConverter {
    public CustomerVO toVO(CustomerEntity entity) {
        if (entity == null) return null;
        CustomerVO vo = new CustomerVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setPhone(entity.getPhone());
        vo.setIdCard(entity.getIdCard());
        vo.setGender(entity.getGender());
        vo.setEmail(entity.getEmail());
        vo.setMembership(entity.getMembership());
        vo.setRegisterTime(entity.getRegisterTime());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
    public List<CustomerVO> toVO(List<CustomerEntity> entities) {
        return entities.stream().map(this::toVO).toList();
    }
    public CustomerEntity toEntity(CustomerSaveDTO dto) {
        if (dto == null) return null;
        CustomerEntity entity = new CustomerEntity();
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setIdCard(dto.getIdCard());
        entity.setGender(dto.getGender());
        entity.setEmail(dto.getEmail());
        entity.setMembership(dto.getMembership());
        entity.setRegisterTime(dto.getRegisterTime());
        return entity;
    }
}
