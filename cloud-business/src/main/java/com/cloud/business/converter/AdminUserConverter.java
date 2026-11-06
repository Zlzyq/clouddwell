package com.cloud.business.converter;

import com.cloud.business.dto.AdminUserSaveDTO;
import com.cloud.business.entity.AdminUserEntity;
import com.cloud.business.vo.AdminUserVO;
import com.cloud.common.base.BaseConverter;
import org.springframework.stereotype.Component;

@Component
public class AdminUserConverter extends BaseConverter<AdminUserEntity, AdminUserVO> {

    @Override
    public AdminUserVO convert(AdminUserEntity source) {
        if (source == null) {
            return null;
        }
        AdminUserVO vo = new AdminUserVO();
        vo.setId(source.getId());
        vo.setLoginName(source.getLoginName());
        vo.setUserName(source.getUserName());
        vo.setDeptName(source.getDeptName());
        vo.setRole(source.getRole());
        vo.setPhone(source.getPhone());
        vo.setStatus(source.getStatus());
        return vo;
    }

    public AdminUserEntity toNewEntity(AdminUserSaveDTO dto) {
        AdminUserEntity entity = new AdminUserEntity();
        applyDto(entity, dto);
        return entity;
    }

    public void applyDto(AdminUserEntity entity, AdminUserSaveDTO dto) {
        entity.setLoginName(dto.getLoginName());
        entity.setUserName(dto.getUserName());
        entity.setDeptName(dto.getDeptName());
        entity.setRole(dto.getRole());
        entity.setPhone(dto.getPhone());
        entity.setStatus(dto.getStatus());
    }
}
