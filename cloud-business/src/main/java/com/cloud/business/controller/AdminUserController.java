package com.cloud.business.controller;

import com.cloud.business.dto.AdminUserSaveDTO;
import com.cloud.business.service.AdminUserService;
import com.cloud.business.vo.AdminUserVO;
import com.cloud.common.base.BaseController;
import com.cloud.common.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "管理中心用户")
public class AdminUserController extends BaseController<AdminUserSaveDTO, AdminUserVO> {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    @Operation(summary = "用户列表")
    public Result<List<AdminUserVO>> list() {
        return ok(adminUserService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "用户详情")
    public Result<AdminUserVO> get(@PathVariable("id") Long id) {
        return ok(adminUserService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增用户")
    public Result<AdminUserVO> create(@Valid @RequestBody AdminUserSaveDTO dto) {
        return ok(adminUserService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改用户")
    public Result<AdminUserVO> update(@PathVariable("id") Long id, @Valid @RequestBody AdminUserSaveDTO dto) {
        return ok(adminUserService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result<Void> delete(@PathVariable("id") Long id) {
        adminUserService.remove(id);
        return ok();
    }

    @PostMapping("/reset")
    @Operation(summary = "重置为种子数据")
    public Result<List<AdminUserVO>> reset() {
        return ok(adminUserService.reset());
    }
}
