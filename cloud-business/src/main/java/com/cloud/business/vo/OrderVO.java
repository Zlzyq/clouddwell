package com.cloud.business.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Schema(description = "订单详情出参")
public class OrderVO implements Serializable {


    private Long orderId;
    private String orderNo;
    private Long buyerUserId;
    private String productCode;
    private Integer quantity;
    private BigDecimal totalAmount;
    private Integer status;
    private String createTimeDisplay;


}
