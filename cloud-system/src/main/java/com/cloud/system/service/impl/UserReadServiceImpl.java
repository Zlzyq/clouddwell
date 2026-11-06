package com.cloud.system.service.impl;

import com.cloud.common.exception.GlobalException;
import com.cloud.system.service.UserReadService;
import com.cloud.system.vo.UserVO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 演示内存用户表。
 */
@Service
public class UserReadServiceImpl implements UserReadService {

    private static final Map<Long, UserVO> DEMO_USERS = new HashMap<>();

    static {
        DEMO_USERS.put(1L, new UserVO(1L, "演示用户一号", "超级管理员", "/img/avatar-1.svg"));
        DEMO_USERS.put(2L, new UserVO(2L, "演示用户二号", "运营专员", "/img/avatar-2.svg"));
        DEMO_USERS.put(3L, new UserVO(3L, "压测用户A", "普通用户", "/img/avatar-3.svg"));
        DEMO_USERS.put(4L, new UserVO(4L, "压测用户B", "普通用户", "/img/avatar-3.svg"));
        DEMO_USERS.put(5L, new UserVO(5L, "压测用户C", "普通用户", "/img/avatar-3.svg"));
        DEMO_USERS.put(6L, new UserVO(6L, "压测用户D", "普通用户", "/img/avatar-3.svg"));
        DEMO_USERS.put(7L, new UserVO(7L, "压测用户E", "普通用户", "/img/avatar-3.svg"));
        DEMO_USERS.put(8L, new UserVO(8L, "压测用户F", "普通用户", "/img/avatar-3.svg"));
        DEMO_USERS.put(9L, new UserVO(9L, "压测用户G", "普通用户", "/img/avatar-3.svg"));
        DEMO_USERS.put(10L, new UserVO(10L, "压测用户H", "普通用户", "/img/avatar-3.svg"));
        DEMO_USERS.put(11L, new UserVO(11L, "压测用户I", "普通用户", "/img/avatar-3.svg"));
        DEMO_USERS.put(12L, new UserVO(12L, "压测用户J", "普通用户", "/img/avatar-3.svg"));
        DEMO_USERS.put(13L, new UserVO(13L, "压测用户K", "普通用户", "/img/avatar-3.svg"));
        DEMO_USERS.put(14L, new UserVO(14L, "压测用户L", "普通用户", "/img/avatar-3.svg"));
        DEMO_USERS.put(15L, new UserVO(15L, "压测用户M", "普通用户", "/img/avatar-3.svg"));
        DEMO_USERS.put(16L, new UserVO(16L, "压测用户N", "普通用户", "/img/avatar-3.svg"));
        DEMO_USERS.put(17L, new UserVO(17L, "压测用户O", "普通用户", "/img/avatar-3.svg"));
        DEMO_USERS.put(18L, new UserVO(18L, "压测用户P", "普通用户", "/img/avatar-3.svg"));
        DEMO_USERS.put(19L, new UserVO(19L, "压测用户Q", "普通用户", "/img/avatar-3.svg"));
        DEMO_USERS.put(20L, new UserVO(20L, "压测用户R", "普通用户", "/img/avatar-3.svg"));
        DEMO_USERS.put(10086L, new UserVO(10086L, "演示用户10086", "普通用户", "/img/avatar-3.svg"));
    }

    @Override
    public UserVO getByUserId(Long userId) {
        if (userId == null) {
            throw new GlobalException("用户不存在");
        }
        UserVO u = DEMO_USERS.get(userId);
        if (u == null) {
            throw new GlobalException("用户不存在");
        }
        return new UserVO(u.getUserId(), u.getUserName(), u.getRole(), u.getAvatarUrl());
    }


}
