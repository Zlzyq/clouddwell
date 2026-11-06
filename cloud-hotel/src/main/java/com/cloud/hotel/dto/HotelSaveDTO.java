package com.cloud.hotel.dto;

public class HotelSaveDTO {
    private String name;
    private String address;
    private String phone;
    private Integer starRating;
    private String description;
    private String status;
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
}
