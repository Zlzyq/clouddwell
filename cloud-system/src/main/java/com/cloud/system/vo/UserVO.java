package com.cloud.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(description = "用户信息")
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String userName;
    private String role;
    private String avatarUrl;

    public UserVO() {
    }

    public UserVO(Long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public UserVO(Long userId, String userName, String role, String avatarUrl) {
        this.userId = userId;
        this.userName = userName;
        this.role = role;
        this.avatarUrl = avatarUrl;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
