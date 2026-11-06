package com.cloud.business.repository;

import com.cloud.business.entity.HotelAmenityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface HotelAmenityRepository extends MongoRepository<HotelAmenityEntity, String> {

    @Query("{ '$or':[{'hotelName':{'$regex': ?0,'$options': 'i'}}" +
            ",{'address':{'$regex': ?0,'$options': 'i'}}" +
            ",{'description':{'$regex': ?0,'$options': 'i'}}" +
            ",{'amenities':{'$regex': ?0,'$options': 'i'}}] }"
    )
    Page<HotelAmenityEntity> search(String keyword, Pageable pageable);

    //Containing就是模糊匹配
    List<HotelAmenityEntity> findByHotelNameStartingWith(String keyword);

    List<HotelAmenityEntity> findByAddressStartingWith(String keyword);
}
