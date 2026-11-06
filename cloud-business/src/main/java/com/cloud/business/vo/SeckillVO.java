package com.cloud.business.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String seckillName;
    private String productCode;
    private BigDecimal seckillPrice;
    private Integer seckillStock;
    private Integer originalStock;
    private Integer limitCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 已售数量（originalStock - seckillStock） */
    private Integer soldCount;
}