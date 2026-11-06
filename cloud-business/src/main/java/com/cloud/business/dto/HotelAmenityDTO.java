package com.cloud.business.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelAmenityDTO {
    private String hotelName;
    private String address;
    private String phone;
    private Integer starRating;
    private String description;
    private List<String> amenities;
    private Integer status;
}
