package com.cloud.system.service;

import com.cloud.system.dto.UserQueryDTO;
import com.cloud.system.vo.UserVO;

/**
 * 用户读服务接口。
 */
public interface UserReadService {

    UserVO getByUserId(Long userId);
}
