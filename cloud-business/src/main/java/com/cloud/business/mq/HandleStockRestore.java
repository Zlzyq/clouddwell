package com.cloud.business.mq;

import com.cloud.business.dto.DeductOrResortStockDTO;
import com.cloud.business.service.ProductService;
import com.cloud.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class HandleStockRestore {
    private final ProductService productService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "stock.restore.queue", durable = "true"),
            exchange = @Exchange(name = "order.direct"),
            key = "restore.deduct"
    ))
    public void handleStockRestore(DeductOrResortStockDTO message) {
        Boolean aBoolean = productService.restoreStock(message.getProductCode(), message.getQuantity());
        if (!aBoolean) {
            throw new GlobalException("恢复库存失败");
        }
    }
}
