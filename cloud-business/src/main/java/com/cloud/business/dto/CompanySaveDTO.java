package com.cloud.business.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class CompanySaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "公司编码不能为空")
    private String code;

    @NotBlank(message = "公司名称不能为空")
    private String name;

    private String contact;
    private String phone;
    private String status;

    public CompanySaveDTO() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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
