package com.cloud.business.feign.dto;

import java.io.Serializable;

/** 与用户服务 JSON 对齐的远程 VO。 */
public class UserRemoteVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String userName;

    public UserRemoteVO() {
    }

    public UserRemoteVO(Long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
