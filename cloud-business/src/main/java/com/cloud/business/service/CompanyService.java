package com.cloud.business.service;

import com.cloud.business.dto.CompanySaveDTO;
import com.cloud.business.vo.CompanyVO;

import java.util.List;

public interface CompanyService {

    List<CompanyVO> listAll();

    CompanyVO getById(Long id);

    CompanyVO create(CompanySaveDTO dto);

    CompanyVO update(Long id, CompanySaveDTO dto);

    void remove(Long id);

    List<CompanyVO> reset();
}
