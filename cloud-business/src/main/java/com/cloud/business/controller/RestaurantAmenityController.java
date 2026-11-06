package com.cloud.business.controller;

import com.cloud.business.entity.RestaurantAmenityEntity;
import com.cloud.business.service.RestaurantAmenityService;
import com.cloud.common.domain.PageResult;
import com.cloud.common.domain.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 饭店待遇 前端控制器
 */
@RestController
@RequestMapping("/api/restaurant-amenities")
@RequiredArgsConstructor
public class RestaurantAmenityController {

    private final RestaurantAmenityService restaurantAmenityService;

    /**
     * 分页查询（含搜索）
     */
    @GetMapping
    public Result<PageResult<RestaurantAmenityEntity>> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            return Result.ok(restaurantAmenityService.search(keyword, page, size));
        }
        return Result.ok(restaurantAmenityService.listAll(page, size));
    }

    /**
     * 查询单个
     */
    @GetMapping("/{id}")
    public Result<RestaurantAmenityEntity> getById(@PathVariable Long id) {
        RestaurantAmenityEntity entity = restaurantAmenityService.getById(id);
        if (entity == null) {
            return Result.fail(404, "内容不存在");
        }
        return Result.ok(entity);
    }

    /**
     * 按名称搜索（测试用）
     */
    @GetMapping("/test/name")
    public Result<List<RestaurantAmenityEntity>> getByName(@RequestParam String name) {
        return Result.ok(restaurantAmenityService.getByName(name));
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<RestaurantAmenityEntity> create(@RequestBody RestaurantAmenityEntity entity) {
        restaurantAmenityService.save(entity);
        return Result.ok(entity);
    }

    /**
     * 修改
     */
    @PutMapping("/{id}")
    public Result<RestaurantAmenityEntity> update(@PathVariable Long id, @RequestBody RestaurantAmenityEntity entity) {
        entity.setId(id);
        restaurantAmenityService.updateById(entity);
        return Result.ok(restaurantAmenityService.getById(id));
    }

    /**
     * 删除单个
     */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        restaurantAmenityService.removeById(id);
        return Result.ok();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public Result<Void> removeBatch(@RequestParam List<Long> ids) {
        restaurantAmenityService.removeBatch(ids);
        return Result.ok();
    }

    /**
     * 生成测试数据
     */
    @PostMapping("/test/init/{count}")
    public Result<String> initTestData(@PathVariable int count) {
        return Result.ok(restaurantAmenityService.initTestData(count));
    }

    /**
     * 清空测试数据（restaurant_name 以"压测-"开头的）
     */
    @DeleteMapping("/clear-test")
    public Result<Void> clearTestData() {
        restaurantAmenityService.clearTestData();
        return Result.ok();
    }

}
