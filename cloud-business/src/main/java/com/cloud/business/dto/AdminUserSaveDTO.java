package com.cloud.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "登录名不能为空")
    private String loginName;

    @NotBlank(message = "姓名不能为空")
    private String userName;

    private String deptName;
    private String role;
    private String phone;
    private String status;

}
