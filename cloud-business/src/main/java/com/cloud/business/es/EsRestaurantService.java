package com.cloud.business.es;


import com.cloud.business.entity.RestaurantAmenityEntity;
import com.cloud.common.domain.PageResult;

public interface EsRestaurantService {
    /**
     * 从 MySQL 全量导入数据到 ES
     */
    String importFromMysql();

    /**
     * ES 搜索（关键字 + 分页）
     */
    PageResult<RestaurantAmenityEntity> searchFromEs(String keyword, String cuisineType, int minRating, String sortBy, String sortOrder, Double lat, Double lon, Integer maxDistance, int page, int size, boolean useSecondaryFilter);
    /**
     * ES 删除
     */
    String clearEs();

}
