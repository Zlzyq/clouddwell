package com.cloud.business.controller;

import cn.hutool.core.convert.Convert;
import com.cloud.business.dto.AdminOrderSaveDTO;
import com.cloud.business.service.AdminOrderService;
import com.cloud.business.vo.AdminOrderVO;
import com.cloud.common.base.BaseController;
import com.cloud.common.domain.PageResult;
import com.cloud.common.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/admin/orders")
@Tag(name = "管理中心订单", description = "与真实下单 API /api/orders 独立")
public class AdminOrderController extends BaseController<AdminOrderSaveDTO, AdminOrderVO> {

    private final AdminOrderService adminOrderService;


    @GetMapping
    @Operation(summary = "订单列表")
    public Result<PageResult<AdminOrderVO>> list(@RequestParam("page")Integer page, @RequestParam("size")Integer size) {
        return ok(adminOrderService.page(page,size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "订单详情")
    public Result<AdminOrderVO> get(@PathVariable("id") Long id) {
        return ok(adminOrderService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增订单")
    public Result<AdminOrderVO> create(@Valid @RequestBody AdminOrderSaveDTO dto) {
        return ok(adminOrderService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改订单")
    public Result<AdminOrderVO> update(@PathVariable("id") Long id, @Valid @RequestBody AdminOrderSaveDTO dto) {
        return ok(adminOrderService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除订单")
    public Result<Void> delete(@PathVariable("id") Long id) {
        adminOrderService.remove(id);
        return ok();
    }

    @PostMapping("/reset")
    @Operation(summary = "重置为种子数据")
    public Result<List<AdminOrderVO>> reset() {
        return ok(adminOrderService.reset());
    }

    @DeleteMapping("/batch")
    public Result<Void> delete(@RequestParam("ids") String ids) {
        List<Long> idList = Convert.toList(Long.class, ids.split(","));
        adminOrderService.batchDelete(idList);
        return ok();
    }

    @GetMapping("/search")
    @Operation(summary = "按买家搜索")
    public Result<List<AdminOrderVO>> search(@RequestParam("keyword") String keyword) {
        return ok(adminOrderService.searchByName(keyword));
    }

}
