package com.cloud.business.controller;

import com.cloud.business.dto.ProductSaveDTO;
import com.cloud.business.service.ProductService;
import com.cloud.business.vo.ProductVO;
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
@RequestMapping("/api/products")
@Tag(name = "产品管理")
public class ProductController extends BaseController<ProductSaveDTO, ProductVO> {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "产品列表")
    public Result<List<ProductVO>> list() {
        return ok(productService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "产品详情")
    public Result<ProductVO> get(@PathVariable("id") Long id) {
        return ok(productService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增产品")
    public Result<ProductVO> create(@Valid @RequestBody ProductSaveDTO dto) {
        return ok(productService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改产品")
    public Result<ProductVO> update(@PathVariable("id") Long id, @Valid @RequestBody ProductSaveDTO dto) {
        return ok(productService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除产品")
    public Result<Void> delete(@PathVariable("id") Long id) {
        productService.remove(id);
        return ok();
    }

    @PostMapping("/reset")
    @Operation(summary = "重置为种子数据")
    public Result<List<ProductVO>> reset() {
        return ok(productService.reset());
    }
}
