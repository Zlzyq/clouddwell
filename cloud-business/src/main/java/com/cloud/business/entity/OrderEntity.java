package com.cloud.business.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 订单表 biz_order */
@TableName("biz_order")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity implements Serializable {


    @TableId
    private Long orderId;

    private String orderNo;

    private Long buyerUserId;

    private String productCode;

    private Integer quantity;

    private BigDecimal totalAmount;

    /**
     * 状态：10=已创建
     */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

