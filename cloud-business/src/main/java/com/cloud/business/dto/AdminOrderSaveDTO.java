package com.cloud.business.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class AdminOrderSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;

    @NotBlank(message = "买家不能为空")
    private String buyerName;

    @NotBlank(message = "SKU 不能为空")
    private String productCode;

    @NotBlank(message = "商品名称不能为空")
    private String productName;

    private Integer quantity;

    @NotBlank(message = "金额不能为空")
    private String amount;

    private String status;
    private String createTime;

    public AdminOrderSaveDTO() {
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
