package com.cloud.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.business.converter.CompanyConverter;
import com.cloud.business.dto.CompanySaveDTO;
import com.cloud.business.entity.CompanyEntity;
import com.cloud.business.mapper.CompanyMapper;
import com.cloud.business.service.CompanyService;
import com.cloud.business.vo.CompanyVO;
import com.cloud.common.exception.GlobalException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, CompanyEntity> implements CompanyService {

    private final CompanyMapper companyMapper;
    private final CompanyConverter companyConverter;

    public CompanyServiceImpl(CompanyMapper companyMapper, CompanyConverter companyConverter) {
        this.companyMapper = companyMapper;
        this.companyConverter = companyConverter;
    }

    @Override
    public List<CompanyVO> listAll() {
        return companyMapper.selectList(null).stream().map(companyConverter::convert).toList();
    }

    @Override
    public CompanyVO getById(Long id) {
        CompanyEntity entity = companyMapper.selectById(id);
        if (entity == null) throw new GlobalException("记录不存在");
        return companyConverter.convert(entity);
    }

    @Override
    public CompanyVO create(CompanySaveDTO dto) {
        CompanyEntity entity = companyConverter.toNewEntity(dto);
        companyMapper.insert(entity);
        return companyConverter.convert(entity);
    }

    @Override
    public CompanyVO update(Long id, CompanySaveDTO dto) {
        CompanyEntity entity = companyMapper.selectById(id);
        if (entity == null) throw new GlobalException("记录不存在");
        companyConverter.applyDto(entity, dto);
        entity.setId(id);
        companyMapper.updateById(entity);
        return companyConverter.convert(entity);
    }

    @Override
    public void remove(Long id) {
        companyMapper.deleteById(id);
    }

    @Override
    public List<CompanyVO> reset() {
        return List.of();
    }
}
