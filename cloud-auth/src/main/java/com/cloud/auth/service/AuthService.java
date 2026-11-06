package com.cloud.auth.service;

import com.cloud.auth.dto.LoginRequest;
import com.cloud.auth.vo.LoginVO;

/**
 * 认证服务接口。
 */
public interface AuthService {

    LoginVO login(LoginRequest request);
}
