package com.cloud.customer.controller;

import com.cloud.common.base.BaseController;
import com.cloud.common.domain.Result;
import com.cloud.customer.converter.CustomerConverter;
import com.cloud.customer.dto.CustomerSaveDTO;
import com.cloud.customer.entity.CustomerEntity;
import com.cloud.customer.service.CustomerService;
import com.cloud.customer.vo.CustomerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "客户管理")
@RestController
@RequestMapping("/api/customers")
public class CustomerController extends BaseController<CustomerSaveDTO, CustomerVO> {
    private final CustomerService customerService;
    private final CustomerConverter customerConverter;
    public CustomerController(CustomerService customerService, CustomerConverter customerConverter) {
        this.customerService = customerService;
        this.customerConverter = customerConverter;
    }
    @Operation(summary = "客户列表")
    @GetMapping
    public Result<List<CustomerVO>> list() { return ok(customerConverter.toVO(customerService.list())); }
    @Operation(summary = "客户详情")
    @GetMapping("/{id}")
    public Result<CustomerVO> get(@PathVariable Long id) { return ok(customerConverter.toVO(customerService.getById(id))); }
    @Operation(summary = "新增客户")
    @PostMapping
    public Result<CustomerVO> save(@RequestBody CustomerSaveDTO dto) {
        CustomerEntity entity = customerConverter.toEntity(dto);
        return ok(customerConverter.toVO(customerService.save(entity)));
    }
    @Operation(summary = "修改客户")
    @PutMapping("/{id}")
    public Result<CustomerVO> update(@PathVariable Long id, @RequestBody CustomerSaveDTO dto) {
        CustomerEntity entity = customerConverter.toEntity(dto);
        return ok(customerConverter.toVO(customerService.updateById(id, entity)));
    }
    @Operation(summary = "删除客户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { customerService.deleteById(id); return ok(); }
}
