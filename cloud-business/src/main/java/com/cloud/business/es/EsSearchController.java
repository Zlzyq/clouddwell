package com.cloud.business.es;

import com.cloud.business.entity.RestaurantAmenityEntity;
import com.cloud.business.service.RestaurantAmenityService;
import com.cloud.common.domain.PageResult;
import com.cloud.common.domain.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/es-restaurants")
@RequiredArgsConstructor
public class EsSearchController {

    private final EsRestaurantService esRestaurantService;
    private final RestaurantAmenityService restaurantAmenityService;

    /**
     * 从 MySQL 全量导入数据到 ES
     */
    @PostMapping("/import")
    public Result<String> importFromMysql() {
        return Result.ok(esRestaurantService.importFromMysql());
    }

    /**
     * ES 搜索
     */
    @GetMapping("/search")
    public Result<PageResult<RestaurantAmenityEntity>> search(@RequestParam String keyword,
                                                              @RequestParam String cuisineType,
                                                              @RequestParam(defaultValue = "0") int minRating,
                                                              @RequestParam(defaultValue = "rating") String sortBy,
                                                              @RequestParam(defaultValue = "desc") String sortOrder,
                                                              @RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "39.9042") double lat,
                                                              @RequestParam(defaultValue = "116.4074") double lon,
                                                              @RequestParam(defaultValue = "0") int maxDistance,
                                                              @RequestParam(defaultValue = "10") int size,
                                                              @RequestParam(defaultValue = "false") boolean useSecondaryFilter) {
        return Result.ok(esRestaurantService.searchFromEs(keyword, cuisineType, minRating, sortBy, sortOrder, lat, lon, maxDistance, page, size, useSecondaryFilter));
    }

    /**
     * MySQL LIKE 'keyword%'（右模糊，可利用 B+ 树索引前缀跳转）
     */
    @GetMapping("/search-right-like")
    public Result<PageResult<RestaurantAmenityEntity>> searchRightLike(@RequestParam String keyword, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        return Result.ok(restaurantAmenityService.searchRightLike(keyword, page, size));
    }

    /**
     * 清空Es数据
     */
    @DeleteMapping("/clear-es")
    public Result<String> clearEsData() {
        return Result.ok(esRestaurantService.clearEs());
    }

}
