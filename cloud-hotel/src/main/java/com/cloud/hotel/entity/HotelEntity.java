package com.cloud.hotel.entity;

import com.cloud.common.domain.Identifiable;

public class HotelEntity implements Identifiable {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private Integer starRating;
    private String description;
    private String status;
    private String createTime;

    @Override public Long getId() { return id; }
    @Override public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getStarRating() { return starRating; }
    public void setStarRating(Integer starRating) { this.starRating = starRating; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
