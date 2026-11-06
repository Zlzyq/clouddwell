package com.cloud.business.converter;

import com.cloud.business.dto.ProductSaveDTO;
import com.cloud.business.entity.ProductEntity;
import com.cloud.business.vo.ProductVO;
import com.cloud.common.base.BaseConverter;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter extends BaseConverter<ProductEntity, ProductVO> {

    @Override
    public ProductVO convert(ProductEntity source) {
        if (source == null) {
            return null;
        }
        ProductVO vo = new ProductVO();
        vo.setId(source.getId());
        vo.setCode(source.getCode());
        vo.setName(source.getName());
        vo.setCategory(source.getCategory());
        vo.setPrice(source.getPrice());
        vo.setStock(source.getStock());
        vo.setStatus(source.getStatus());
        return vo;
    }

    public ProductEntity toNewEntity(ProductSaveDTO dto) {
        ProductEntity entity = new ProductEntity();
        applyDto(entity, dto);
        return entity;
    }

    public void applyDto(ProductEntity entity, ProductSaveDTO dto) {
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setCategory(dto.getCategory());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        entity.setStatus(dto.getStatus());
    }
}
