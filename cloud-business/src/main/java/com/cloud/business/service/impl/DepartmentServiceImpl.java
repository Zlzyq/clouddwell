package com.cloud.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.business.converter.DepartmentConverter;
import com.cloud.business.dto.DepartmentSaveDTO;
import com.cloud.business.entity.DepartmentEntity;
import com.cloud.business.mapper.DepartmentMapper;
import com.cloud.business.service.DepartmentService;
import com.cloud.business.vo.DepartmentVO;
import com.cloud.common.exception.GlobalException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, DepartmentEntity> implements DepartmentService {

    private final DepartmentMapper departmentMapper;
    private final DepartmentConverter departmentConverter;

    public DepartmentServiceImpl(DepartmentMapper departmentMapper, DepartmentConverter departmentConverter) {
        this.departmentMapper = departmentMapper;
        this.departmentConverter = departmentConverter;
    }

    @Override
    public List<DepartmentVO> listAll() {
        return departmentMapper.selectList(null).stream().map(departmentConverter::convert).toList();
    }

    @Override
    public DepartmentVO getById(Long id) {
        DepartmentEntity entity = departmentMapper.selectById(id);
        if (entity == null) throw new GlobalException("记录不存在");
        return departmentConverter.convert(entity);
    }

    @Override
    public DepartmentVO create(DepartmentSaveDTO dto) {
        DepartmentEntity entity = departmentConverter.toNewEntity(dto);
        departmentMapper.insert(entity);
        return departmentConverter.convert(entity);
    }

    @Override
    public DepartmentVO update(Long id, DepartmentSaveDTO dto) {
        DepartmentEntity entity = departmentMapper.selectById(id);
        if (entity == null) throw new GlobalException("记录不存在");
        departmentConverter.applyDto(entity, dto);
        entity.setId(id);
        departmentMapper.updateById(entity);
        return departmentConverter.convert(entity);
    }

    @Override
    public void remove(Long id) {
        departmentMapper.deleteById(id);
    }

    @Override
    public List<DepartmentVO> reset() {
        return List.of();
    }
}
