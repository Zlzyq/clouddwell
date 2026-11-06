package com.cloud.business.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class DepartmentSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "所属公司不能为空")
    private String companyName;

    @NotBlank(message = "部门名称不能为空")
    private String name;

    private String leader;
    private String phone;
    private String status;

    public DepartmentSaveDTO() {
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
