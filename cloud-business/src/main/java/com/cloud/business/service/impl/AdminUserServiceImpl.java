package com.cloud.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.business.converter.AdminUserConverter;
import com.cloud.business.dto.AdminUserSaveDTO;
import com.cloud.business.entity.AdminUserEntity;
import com.cloud.business.mapper.AdminUserMapper;
import com.cloud.business.service.AdminUserService;
import com.cloud.business.vo.AdminUserVO;
import com.cloud.common.exception.GlobalException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUserEntity> implements AdminUserService {

    private final AdminUserMapper adminUserMapper;
    private final AdminUserConverter adminUserConverter;

    public AdminUserServiceImpl(AdminUserMapper adminUserMapper, AdminUserConverter adminUserConverter) {
        this.adminUserMapper = adminUserMapper;
        this.adminUserConverter = adminUserConverter;
    }

    @Override
    public List<AdminUserVO> listAll() {
        return adminUserMapper.selectList(null).stream().map(adminUserConverter::convert).toList();
    }

    @Override
    public AdminUserVO getById(Long id) {
        AdminUserEntity entity = adminUserMapper.selectById(id);
        if (entity == null) throw new GlobalException("记录不存在");
        return adminUserConverter.convert(entity);
    }

    @Override
    public AdminUserVO create(AdminUserSaveDTO dto) {
        AdminUserEntity entity = adminUserConverter.toNewEntity(dto);
        adminUserMapper.insert(entity);
        return adminUserConverter.convert(entity);
    }

    @Override
    public AdminUserVO update(Long id, AdminUserSaveDTO dto) {
        AdminUserEntity entity = adminUserMapper.selectById(id);
        if (entity == null) throw new GlobalException("记录不存在");
        adminUserConverter.applyDto(entity, dto);
        entity.setId(id);
        adminUserMapper.updateById(entity);
        return adminUserConverter.convert(entity);
    }

    @Override
    public void remove(Long id) {
        adminUserMapper.deleteById(id);
    }

    @Override
    public List<AdminUserVO> reset() {
        return List.of();
    }
}
