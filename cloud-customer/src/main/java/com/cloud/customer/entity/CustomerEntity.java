package com.cloud.customer.entity;

import com.cloud.common.domain.Identifiable;

public class CustomerEntity implements Identifiable {
    private Long id;
    private String name;
    private String phone;
    private String idCard;
    private String gender;
    private String email;
    private String membership;
    private String registerTime;
    private String createTime;

    @Override public Long getId() { return id; }
    @Override public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMembership() { return membership; }
    public void setMembership(String membership) { this.membership = membership; }
    public String getRegisterTime() { return registerTime; }
    public void setRegisterTime(String registerTime) { this.registerTime = registerTime; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
