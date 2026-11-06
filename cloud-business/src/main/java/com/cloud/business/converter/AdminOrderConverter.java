package com.cloud.business.converter;

import com.cloud.business.dto.AdminOrderSaveDTO;
import com.cloud.business.entity.AdminOrderEntity;
import com.cloud.business.vo.AdminOrderVO;
import com.cloud.common.base.BaseConverter;
import com.cloud.common.util.DateUtils;
import org.springframework.stereotype.Component;

@Component
public class AdminOrderConverter extends BaseConverter<AdminOrderEntity, AdminOrderVO> {

    @Override
    public AdminOrderVO convert(AdminOrderEntity source) {
        if (source == null) {
            return null;
        }
        AdminOrderVO vo = new AdminOrderVO();
        vo.setId(source.getId());
        vo.setOrderNo(source.getOrderNo());
        vo.setBuyerName(source.getBuyerName());
        vo.setProductCode(source.getProductCode());
        vo.setProductName(source.getProductName());
        vo.setQuantity(source.getQuantity());
        vo.setAmount(source.getAmount());
        vo.setStatus(source.getStatus());
        vo.setCreateTime(source.getCreateTime());
        return vo;
    }

    public AdminOrderEntity toNewEntity(AdminOrderSaveDTO dto) {
        AdminOrderEntity entity = new AdminOrderEntity();
        applyDto(entity, dto);
        if (entity.getOrderNo() == null || entity.getOrderNo().isBlank()) {
            entity.setOrderNo("ORD" + System.currentTimeMillis());
        }
        if (entity.getCreateTime() == null || entity.getCreateTime().isBlank()) {
            entity.setCreateTime(DateUtils.formatNow());
        }
        return entity;
    }

    public void applyDto(AdminOrderEntity entity, AdminOrderSaveDTO dto) {
        entity.setOrderNo(dto.getOrderNo());
        entity.setBuyerName(dto.getBuyerName());
        entity.setProductCode(dto.getProductCode());
        entity.setProductName(dto.getProductName());
        entity.setQuantity(dto.getQuantity());
        entity.setAmount(dto.getAmount());
        entity.setStatus(dto.getStatus());
        entity.setCreateTime(dto.getCreateTime());
    }
}
