package com.cloud.system.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/** 用户查询入参骨架。 */
public class UserQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Long userId;

    public UserQueryDTO() {
    }

    public UserQueryDTO(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
