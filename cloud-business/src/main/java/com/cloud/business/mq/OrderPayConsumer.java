package com.cloud.business.mq;

import com.cloud.business.dto.OrderPayMessageDTO;
import com.cloud.business.service.OrderService;
import com.cloud.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderPayConsumer {


    private final OrderService orderService;

    //spring默认把交换机和消息队列都设置成持久化declare = "true"就是冗余的
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "order.pay.queue"), exchange = @Exchange(name = "order.direct"), key = "order.pay"))
    public void handlePayMessage(OrderPayMessageDTO message) {
        //判断支付的是否是本人
        if (message.getUserId() == null) {
            throw new GlobalException("支付用户信息缺失");
        }
        try {
            orderService.payOrder(message.getOrderNo(), message.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
