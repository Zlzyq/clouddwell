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
public class StockDeductConsumer {

    private final ProductService productService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "stock.deduct.queue"), exchange = @Exchange(name = "order.direct"), key = "stock.deduct"))
    public void handleStockDeduct(DeductOrResortStockDTO message) {
        Boolean aBoolean = productService.deductStock(message.getProductCode(), message.getQuantity());
        if (!aBoolean) {
            throw new GlobalException("扣库存失败");
        }
    }
}
