package com.cloud.business.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class ProductSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "SKU 编码不能为空")
    private String code;

    @NotBlank(message = "产品名称不能为空")
    private String name;

    private String category;

    @NotBlank(message = "单价不能为空")
    private String price;

    private Integer stock;
    private String status;

    public ProductSaveDTO() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
