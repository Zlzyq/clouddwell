package com.cloud.business.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("hotel_amenity")
@CompoundIndex(def = "{'hotelName','status'}")
public class HotelAmenityEntity {
    //唯一索引
    @Id
    private String id;
    //单字段索引
    @Indexed
    private String hotelName;
    //不加索引
    private String address;

    private String phone;
    private Integer starRating;
    private String description;
    private List<String> amenities;
    private Integer status;
}
