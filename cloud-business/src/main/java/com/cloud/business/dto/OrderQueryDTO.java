package com.cloud.business.dto;

import jakarta.validation.constraints.Min;

import java.io.Serializable;

/** 订单列表分页查询占位 DTO。 */
public class OrderQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;

    @Min(1)
    private long pageNo = 1;

    @Min(1)
    private long pageSize = 10;

    public OrderQueryDTO() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public long getPageNo() {
        return pageNo;
    }

    public void setPageNo(long pageNo) {
        this.pageNo = pageNo;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }
}
