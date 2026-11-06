package com.cloud.business.controller;

import com.cloud.business.dto.HotelAmenityDTO;
import com.cloud.business.entity.HotelAmenityEntity;
import com.cloud.business.service.HotelAmenityService;
import com.cloud.common.domain.PageResult;
import com.cloud.common.domain.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotel-amenities")
@RequiredArgsConstructor
public class HotelAmenityController {

    private final HotelAmenityService hotelAmenityService;

    @GetMapping
    public Result<PageResult<HotelAmenityEntity>> listAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            return Result.ok(hotelAmenityService.search(keyword, page, size));
        }
        return Result.ok(hotelAmenityService.listAll(page, size));
    }

    @GetMapping("/test/id")
    public Result<HotelAmenityEntity> testById(@RequestParam String id) {
        return Result.ok(hotelAmenityService.getById(id));
    }

    @GetMapping("/{id}")
    public Result<HotelAmenityEntity> getById(@PathVariable String id) {
        return Result.ok(hotelAmenityService.getById(id));
    }

    @GetMapping("/test/name")
    public Result<List<HotelAmenityEntity>> getByName(@RequestParam String hotelName) {
        return Result.ok(hotelAmenityService.getByName(hotelName));
    }

    @GetMapping("/test/address")
    public Result<List<HotelAmenityEntity>> getByAddress(@RequestParam String address) {
        return Result.ok(hotelAmenityService.getByAddress(address));
    }

    @PostMapping
    public Result<HotelAmenityEntity> create(@RequestBody HotelAmenityDTO dto) {
        return Result.ok(hotelAmenityService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<HotelAmenityEntity> update(@PathVariable String id, @RequestBody HotelAmenityDTO dto) {
        return Result.ok(hotelAmenityService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable String id) {
        hotelAmenityService.remove(id);
        return Result.ok();
    }

    @DeleteMapping("/batch")
    public Result<Void> removeBatch(@RequestParam List<String> ids) {
        hotelAmenityService.removeBatch(ids);
        return Result.ok();
    }

    /**
     * 清空压测产生的测试数据（名字以"压测"开头的）
     */
    @DeleteMapping("/clear-test")
    public Result<Void> clearTestData() {
        hotelAmenityService.clearTestData();
        return Result.ok();
    }

    /**
     * 生成测试数据用于压测
     */
    @PostMapping("/test/init/{count}")
    public Result<String> initTestData(@PathVariable int count) {
        return Result.ok(hotelAmenityService.initTestData(count));
    }
}