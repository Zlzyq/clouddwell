package com.cloud.business.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 后台订单表 biz_admin_order（与真实下单 API 独立）
 */
@TableName("biz_admin_order")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class AdminOrderEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId//标记是主键

    private Long id;
    private String orderNo;
    private String buyerName;
    private String productCode;
    private String productName;
    private Integer quantity;
    private String amount;
    private String status;
    private String createTime;


}
