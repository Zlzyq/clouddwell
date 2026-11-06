package com.cloud.business.converter;

import com.cloud.business.dto.DepartmentSaveDTO;
import com.cloud.business.entity.DepartmentEntity;
import com.cloud.business.vo.DepartmentVO;
import com.cloud.common.base.BaseConverter;
import org.springframework.stereotype.Component;

@Component
public class DepartmentConverter extends BaseConverter<DepartmentEntity, DepartmentVO> {

    @Override
    public DepartmentVO convert(DepartmentEntity source) {
        if (source == null) {
            return null;
        }
        DepartmentVO vo = new DepartmentVO();
        vo.setId(source.getId());
        vo.setCompanyName(source.getCompanyName());
        vo.setName(source.getName());
        vo.setLeader(source.getLeader());
        vo.setPhone(source.getPhone());
        vo.setStatus(source.getStatus());
        return vo;
    }

    public DepartmentEntity toNewEntity(DepartmentSaveDTO dto) {
        DepartmentEntity entity = new DepartmentEntity();
        applyDto(entity, dto);
        return entity;
    }

    public void applyDto(DepartmentEntity entity, DepartmentSaveDTO dto) {
        entity.setCompanyName(dto.getCompanyName());
        entity.setName(dto.getName());
        entity.setLeader(dto.getLeader());
        entity.setPhone(dto.getPhone());
        entity.setStatus(dto.getStatus());
    }
}
