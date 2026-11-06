package com.cloud.room.vo;

import java.math.BigDecimal;

public class RoomVO {
    private Long id;
    private String roomNumber;
    private String type;
    private BigDecimal price;
    private Integer capacity;
    private String status;
    private Long hotelId;
    private String description;
    private String createTime;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
