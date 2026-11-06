package com.cloud.system.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class ProductPriceQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    private String productCode;

    public ProductPriceQueryDTO() {
    }

    public ProductPriceQueryDTO(String productCode) {
        this.productCode = productCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
