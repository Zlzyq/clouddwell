package com.cloud.business.service.impl;

import com.cloud.business.dto.DeductOrResortStockDTO;
import com.cloud.business.dto.OrderPayMessageDTO;
import com.cloud.business.dto.OrderStatusSyncDTO;
import com.cloud.business.dto.SeckillStockDeductDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public record OrderAsyncNotifyService(RabbitTemplate rabbitTemplate) {

    public void notifyOrderCreated(String orderNo, Long userId, String traceId) {
        OrderPayMessageDTO message = new OrderPayMessageDTO(orderNo, userId, traceId);
        rabbitTemplate.convertAndSend("order.direct", "order.pay", message);
    }

    public void notifyOrderTimeout(String orderNo, Long userId, String traceId) {
        OrderPayMessageDTO message = new OrderPayMessageDTO(orderNo, userId, traceId);
        rabbitTemplate.convertAndSend("order.direct", "order.pay.timeout", message);
    }

    public void notifyStockDeduct(String productCode, Long quantity) {
        DeductOrResortStockDTO message = new DeductOrResortStockDTO(productCode, quantity);
        rabbitTemplate.convertAndSend("order.direct", "stock.deduct", message);

    }
    public void notifySeckillStockDeduct(Long seckillId, Long quantity) {
        SeckillStockDeductDTO message = new SeckillStockDeductDTO(seckillId, quantity);
        rabbitTemplate.convertAndSend("order.direct", "seckill.stock.deduct", message);
    }

    public void notifyStockRestore(String productCode, Long quantity) {
        DeductOrResortStockDTO message = new DeductOrResortStockDTO(productCode, quantity);
        rabbitTemplate.convertAndSend("order.direct", "restore.deduct", message);

    }

    public void notifyOrderStatusSync(String orderNo, String status) {
        rabbitTemplate.convertAndSend("order.direct", "order.status.sync", new OrderStatusSyncDTO(orderNo, status));
    }

}
