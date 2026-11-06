package com.cloud.business.controller;

import com.cloud.business.dto.CompanySaveDTO;
import com.cloud.business.service.CompanyService;
import com.cloud.business.vo.CompanyVO;
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
@RequestMapping("/api/companies")
@Tag(name = "公司管理")
public class CompanyController extends BaseController<CompanySaveDTO, CompanyVO> {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    @Operation(summary = "公司列表")
    public Result<List<CompanyVO>> list() {
        return ok(companyService.listAll());
    }

    /**
     *  公司详情
     * @param id 公司ID
     * @return 公司详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "公司详情")
    public Result<CompanyVO> get(@PathVariable("id") Long id) {
        return ok(companyService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增公司")
    public Result<CompanyVO> create(@Valid @RequestBody CompanySaveDTO dto) {
        return ok(companyService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改公司")
    public Result<CompanyVO> update(@PathVariable("id") Long id, @Valid @RequestBody CompanySaveDTO dto) {
        return ok(companyService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除公司")
    public Result<Void> delete(@PathVariable("id") Long id) {
        companyService.remove(id);
        return ok();
    }

    @PostMapping("/reset")
    @Operation(summary = "重置为种子数据")
    public Result<List<CompanyVO>> reset() {
        return ok(companyService.reset());
    }
}
