package com.cloud.business.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 订单状态。 */
@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    CREATED(10, "已创建"),
    PAID(20, "已支付"),
    CANCELLED(30,"已取消"),
    TIMEOUT(40, "已超时"),
    REFUNDING(50, "退款中"),
    REFUNDED(60, "已退款");
    private final int code;
    private final String desc;

}
