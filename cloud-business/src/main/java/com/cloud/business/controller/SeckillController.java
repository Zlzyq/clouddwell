package com.cloud.business.controller;


import com.cloud.business.dto.SeckillOrderDTO;
import com.cloud.business.dto.SeckillSaveDTO;
import com.cloud.business.service.SeckillService;
import com.cloud.business.vo.SeckillVO;
import com.cloud.common.context.UserContext;
import com.cloud.common.domain.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 秒杀活动表 前端控制器
 * </p>
 *
 * @author 张德帅
 * @since 2026-07-10
 */
@Validated
@RestController
@RequestMapping("/api/seckill")
@RequiredArgsConstructor
public class SeckillController {

    private final SeckillService seckillService;

    @GetMapping
    public Result<List<SeckillVO>> list() {
        return Result.ok(seckillService.listAll());
    }

    @GetMapping("/{id}")
    public Result<SeckillVO> get(@PathVariable Long id) {
        return Result.ok(seckillService.getByIdMe(id));
    }

    @PostMapping
    public Result<SeckillVO> create(@Valid @RequestBody SeckillSaveDTO dto) {
        return Result.ok(seckillService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<SeckillVO> update(@PathVariable Long id, @Valid @RequestBody SeckillSaveDTO dto) {
        return Result.ok(seckillService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        seckillService.remove(id);
        return Result.ok();
    }
    @PostMapping("/{id}/order")
    public Result<Void> seckillOrder(@PathVariable Long id, @Valid @RequestBody SeckillOrderDTO dto) {
        Long uid = UserContext.getUid();
        if (uid == null) return Result.fail(401, "请先登录");
        seckillService.seckillOrder(id, uid, dto.getQuantity());
        return Result.ok();
    }
}
