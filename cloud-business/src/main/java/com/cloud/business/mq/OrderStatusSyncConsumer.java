package com.cloud.business.mq;

import com.cloud.business.dto.OrderStatusSyncDTO;
import com.cloud.business.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStatusSyncConsumer {

    private final AdminOrderService adminOrderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("order.status.sync.queue"),
            exchange = @Exchange("order.direct"),
            key = "order.status.sync"
    ))
    public void handleStatusSync(OrderStatusSyncDTO dto) {
        adminOrderService.syncStatus(dto.getOrderNo(), dto.getStatus());
    }
}
