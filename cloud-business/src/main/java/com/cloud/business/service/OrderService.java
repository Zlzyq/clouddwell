package com.cloud.business.service;

import com.cloud.business.dto.SubmitOrderDTO;
import com.cloud.business.vo.OrderVO;

/**
 * 订单服务。
 */
public interface OrderService {

    OrderVO submitOrder(SubmitOrderDTO dto);

    void cancelOrder(Long userId, String orderNo);

    void refund(Long userId, String orderNo);

    void payOrder(String orderNo, Long userId);
}
