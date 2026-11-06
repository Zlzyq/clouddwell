package com.cloud.business.mq;

import com.cloud.business.dto.SeckillStockDeductDTO;
import com.cloud.business.entity.SeckillEntity;
import com.cloud.business.service.SeckillService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SeckillStockDeductConsumer {

    private final SeckillService seckillService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "seckill.stock.deduct.queue"),
            exchange = @Exchange(name = "order.direct"),
            key = "seckill.stock.deduct"))
    public void handleSeckillStockDeduct(SeckillStockDeductDTO message) {
        seckillService.lambdaUpdate()
                .eq(SeckillEntity::getId, message.getSeckillId())
                .ge(SeckillEntity::getSeckillStock, message.getQuantity())
                .setSql("seckill_stock = seckill_stock - " + message.getQuantity())
                .update();
    }
}
