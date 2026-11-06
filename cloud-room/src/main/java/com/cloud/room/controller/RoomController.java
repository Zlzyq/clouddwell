package com.cloud.room.controller;

import com.cloud.common.base.BaseController;
import com.cloud.common.domain.Result;
import com.cloud.room.converter.RoomConverter;
import com.cloud.room.dto.RoomSaveDTO;
import com.cloud.room.entity.RoomEntity;
import com.cloud.room.service.RoomService;
import com.cloud.room.vo.RoomVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "房间管理")
@RestController
@RequestMapping("/api/rooms")
public class RoomController extends BaseController<RoomSaveDTO, RoomVO> {
    private final RoomService roomService;
    private final RoomConverter roomConverter;
    public RoomController(RoomService roomService, RoomConverter roomConverter) {
        this.roomService = roomService;
        this.roomConverter = roomConverter;
    }
    @Operation(summary = "房间列表")
    @GetMapping
    public Result<List<RoomVO>> list() { return ok(roomConverter.toVO(roomService.list())); }
    @Operation(summary = "房间详情")
    @GetMapping("/{id}")
    public Result<RoomVO> get(@PathVariable Long id) { return ok(roomConverter.toVO(roomService.getById(id))); }
    @Operation(summary = "新增房间")
    @PostMapping
    public Result<RoomVO> save(@RequestBody RoomSaveDTO dto) {
        RoomEntity entity = roomConverter.toEntity(dto);
        return ok(roomConverter.toVO(roomService.save(entity)));
    }
    @Operation(summary = "修改房间")
    @PutMapping("/{id}")
    public Result<RoomVO> update(@PathVariable Long id, @RequestBody RoomSaveDTO dto) {
        RoomEntity entity = roomConverter.toEntity(dto);
        return ok(roomConverter.toVO(roomService.updateById(id, entity)));
    }
    @Operation(summary = "删除房间")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { roomService.deleteById(id); return ok(); }
}
