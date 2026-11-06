package com.cloud.system.controller;

import com.cloud.common.base.BaseController;
import com.cloud.common.domain.Result;
import com.cloud.system.dto.ProductPriceQueryDTO;
import com.cloud.system.service.ProductReadService;
import com.cloud.system.vo.ProductPriceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/system/products")
@Tag(name = "商品询价")
public class SystemProductController extends BaseController<ProductPriceQueryDTO, ProductPriceVO> {

    private final ProductReadService productReadService;

    public SystemProductController(ProductReadService productReadService) {
        this.productReadService = productReadService;
    }

    @GetMapping("/price")
    @Operation(summary = "商品价格")
    public Result<ProductPriceVO> price(@RequestParam("code") @NotBlank String code) {
        ProductPriceQueryDTO dto = new ProductPriceQueryDTO(code);
        return ok(productReadService.getPrice(dto));
    }
}
