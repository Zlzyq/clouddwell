package com.cloud.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "商品价格")
public class ProductPriceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String productCode;
    private BigDecimal unitPrice;

    public ProductPriceVO() {
    }

    public ProductPriceVO(String productCode, BigDecimal unitPrice) {
        this.productCode = productCode;
        this.unitPrice = unitPrice;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
