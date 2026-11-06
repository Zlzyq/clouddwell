package com.cloud.business.service;

import com.cloud.business.dto.AdminUserSaveDTO;
import com.cloud.business.vo.AdminUserVO;

import java.util.List;

public interface AdminUserService {

    List<AdminUserVO> listAll();

    AdminUserVO getById(Long id);

    AdminUserVO create(AdminUserSaveDTO dto);

    AdminUserVO update(Long id, AdminUserSaveDTO dto);

    void remove(Long id);

    List<AdminUserVO> reset();
}
