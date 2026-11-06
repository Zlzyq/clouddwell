package com.cloud.business.dto;

import jakarta.validation.constraints.Min;

import java.io.Serializable;

/** 列表/分页查询占位 DTO。 */
public class EmptyQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Min(1)
    private long pageNo = 1;

    @Min(1)
    private long pageSize = 10;

    public EmptyQueryDTO() {
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
