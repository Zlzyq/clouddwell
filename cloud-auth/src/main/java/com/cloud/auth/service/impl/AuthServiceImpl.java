package com.cloud.auth.service.impl;

import com.cloud.auth.demo.DemoUserProfile;
import com.cloud.auth.dto.LoginRequest;
import com.cloud.auth.service.AuthService;
import com.cloud.auth.vo.LoginVO;
import com.cloud.common.exception.GlobalException;
import com.cloud.common.properties.JwtProperties;
import com.cloud.common.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证实现：固定密码，签发 JWT（含 uid claim）。
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final String FIXED_PASSWORD = "admin888";
    private static final java.util.Map<Long, String> SPECIAL_PASSWORDS = java.util.Map.of(
        10010L, "xiongyazhidashazhi"
    );

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    public AuthServiceImpl(JwtUtil jwtUtil, JwtProperties jwtProperties) {
        this.jwtUtil = jwtUtil;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public LoginVO login(LoginRequest request) {
        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new GlobalException("用户ID非法");
        }
        String expectedPwd = SPECIAL_PASSWORDS.getOrDefault(request.getUserId(), FIXED_PASSWORD);
        if (!expectedPwd.equals(request.getPassword())) {
            throw new GlobalException("用户名或密码错误");
        }
        DemoUserProfile profile = DemoUserProfile.of(request.getUserId());
        if (profile == null) {
            throw new GlobalException("用户不存在");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", request.getUserId());
        claims.put("role", profile.getRole());
        String token = jwtUtil.createAccessToken(String.valueOf(request.getUserId()), claims);
        LoginVO vo = new LoginVO(token, jwtProperties.getAccessTokenTtlSeconds());
        vo.setUserId(profile.getUserId());
        vo.setUserName(profile.getUserName());
        vo.setRole(profile.getRole());
        vo.setAvatarUrl(profile.getAvatarUrl());
        return vo;
    }


}
