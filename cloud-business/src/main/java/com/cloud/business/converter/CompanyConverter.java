package com.cloud.business.converter;

import com.cloud.business.dto.CompanySaveDTO;
import com.cloud.business.entity.CompanyEntity;
import com.cloud.business.vo.CompanyVO;
import com.cloud.common.base.BaseConverter;
import com.cloud.common.util.DateUtils;
import org.springframework.stereotype.Component;

@Component
public class CompanyConverter extends BaseConverter<CompanyEntity, CompanyVO> {

    @Override
    public CompanyVO convert(CompanyEntity source) {
        if (source == null) {
            return null;
        }
        CompanyVO vo = new CompanyVO();
        vo.setId(source.getId());
        vo.setCode(source.getCode());
        vo.setName(source.getName());
        vo.setContact(source.getContact());
        vo.setPhone(source.getPhone());
        vo.setStatus(source.getStatus());
        vo.setCreateTime(source.getCreateTime());
        return vo;
    }

    public CompanyEntity toNewEntity(CompanySaveDTO dto) {
        CompanyEntity entity = new CompanyEntity();
        applyDto(entity, dto);
        if (entity.getCreateTime() == null || entity.getCreateTime().isBlank()) {
            entity.setCreateTime(DateUtils.formatNow());
        }
        return entity;
    }

    public void applyDto(CompanyEntity entity, CompanySaveDTO dto) {
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setContact(dto.getContact());
        entity.setPhone(dto.getPhone());
        entity.setStatus(dto.getStatus());
    }
}
