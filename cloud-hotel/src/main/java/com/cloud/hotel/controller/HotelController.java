package com.cloud.hotel.controller;

import com.cloud.common.base.BaseController;
import com.cloud.common.domain.Result;
import com.cloud.hotel.converter.HotelConverter;
import com.cloud.hotel.dto.HotelSaveDTO;
import com.cloud.hotel.entity.HotelEntity;
import com.cloud.hotel.service.HotelService;
import com.cloud.hotel.vo.HotelVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "酒店管理")
@RestController
@RequestMapping("/api/hotels")
public class HotelController extends BaseController<HotelSaveDTO, HotelVO> {
    private final HotelService hotelService;
    private final HotelConverter hotelConverter;
    public HotelController(HotelService hotelService, HotelConverter hotelConverter) {
        this.hotelService = hotelService;
        this.hotelConverter = hotelConverter;
    }
    @Operation(summary = "酒店列表")
    @GetMapping
    public Result<List<HotelVO>> list() { return ok(hotelConverter.toVO(hotelService.list())); }
    @Operation(summary = "酒店详情")
    @GetMapping("/{id}")
    public Result<HotelVO> get(@PathVariable Long id) { return ok(hotelConverter.toVO(hotelService.getById(id))); }
    @Operation(summary = "新增酒店")
    @PostMapping
    public Result<HotelVO> save(@RequestBody HotelSaveDTO dto) {
        HotelEntity entity = hotelConverter.toEntity(dto);
        return ok(hotelConverter.toVO(hotelService.save(entity)));
    }
    @Operation(summary = "修改酒店")
    @PutMapping("/{id}")
    public Result<HotelVO> update(@PathVariable Long id, @RequestBody HotelSaveDTO dto) {
        HotelEntity entity = hotelConverter.toEntity(dto);
        return ok(hotelConverter.toVO(hotelService.updateById(id, entity)));
    }
    @Operation(summary = "删除酒店")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { hotelService.deleteById(id); return ok(); }
}
