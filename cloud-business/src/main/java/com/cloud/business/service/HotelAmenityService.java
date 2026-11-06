package com.cloud.business.service;

import com.cloud.business.dto.HotelAmenityDTO;
import com.cloud.business.entity.HotelAmenityEntity;
import com.cloud.common.domain.PageResult;

import java.util.List;

public interface HotelAmenityService {

    HotelAmenityEntity getById(String id);

    PageResult<HotelAmenityEntity> listAll(int pageNo, int pageSize);

    HotelAmenityEntity create(HotelAmenityDTO dto);

    HotelAmenityEntity update(String id, HotelAmenityDTO dto);

    void remove(String id);

    PageResult<HotelAmenityEntity> search(String keyword, int pageNo, int pageSize);

    void removeBatch(List<String> ids);

    /** 生成随机测试数据用于压测 */
    String initTestData(int count);

    /** 清除所有测试数据（hotelName 以"压测"开头的） */
    void clearTestData();

    List<HotelAmenityEntity> getByName(String hotelName);

    List<HotelAmenityEntity> getByAddress(String address);
}
