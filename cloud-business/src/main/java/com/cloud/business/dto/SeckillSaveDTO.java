package com.cloud.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "秒杀活动名称不能为空")
    private String seckillName;

    @NotBlank(message = "商品编码不能为空")
    private String productCode;

    @NotNull(message = "秒杀价不能为空")
    private BigDecimal seckillPrice;

    @NotNull(message = "秒杀库存不能为空")
    private Integer seckillStock;

    @NotNull(message = "限购数量不能为空")
    private Integer limitCount;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @NotNull(message = "状态不能为空")
    private Integer status;
}