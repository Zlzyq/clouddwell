package com.cloud.business.es;

import com.cloud.business.entity.RestaurantAmenityEntity;
import com.cloud.common.domain.PageResult;

/**
 * 智能派单引擎 — 基于饭店 ES 索引做 geo_distance + 评分加权推荐
 */
public interface EsDispatchService {

    /**
     * 智能推荐饭店
     *
     * @param keyword    关键词（饭店名、菜系等）
     * @param cuisineType 菜系筛选（为空就不限制）
     * @param lat        客户纬度
     * @param lon        客户经度
     * @param maxDistance 最大距离（公里），0 表示不限制
     * @param page       页码
     * @param size       每页条数
     * @return 按综合评分排序的饭店列表（附加距离信息）
     */
    PageResult<RestaurantAmenityEntity> recommend(String keyword, String cuisineType,
                                                   double lat, double lon,
                                                   int maxDistance,
                                                   int page, int size);
}
