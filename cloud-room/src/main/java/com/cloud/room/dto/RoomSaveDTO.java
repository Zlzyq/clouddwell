package com.cloud.room.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomSaveDTO {
    private String roomNumber;
    private String type;
    private BigDecimal price;
    private Integer capacity;
    private String status;
    private Long hotelId;
    private String description;

}
