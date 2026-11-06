package com.cloud.business.controller;

import com.cloud.business.dto.DepartmentSaveDTO;
import com.cloud.business.service.DepartmentService;
import com.cloud.business.vo.DepartmentVO;
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
@RequestMapping("/api/departments")
@Tag(name = "部门管理")
public class DepartmentController extends BaseController<DepartmentSaveDTO, DepartmentVO> {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    @Operation(summary = "部门列表")
    public Result<List<DepartmentVO>> list() {
        return ok(departmentService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "部门详情")
    public Result<DepartmentVO> get(@PathVariable("id") Long id) {
        return ok(departmentService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增部门")
    public Result<DepartmentVO> create(@Valid @RequestBody DepartmentSaveDTO dto) {
        return ok(departmentService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改部门")
    public Result<DepartmentVO> update(@PathVariable("id") Long id, @Valid @RequestBody DepartmentSaveDTO dto) {
        return ok(departmentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门")
    public Result<Void> delete(@PathVariable("id") Long id) {
        departmentService.remove(id);
        return ok();
    }

    @PostMapping("/reset")
    @Operation(summary = "重置为种子数据")
    public Result<List<DepartmentVO>> reset() {
        return ok(departmentService.reset());
    }
}
