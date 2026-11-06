package com.cloud.business.mq;

import com.cloud.business.common.enums.OrderStatusEnum;
import com.cloud.business.dto.OrderPayMessageDTO;
import com.cloud.business.entity.OrderEntity;
import com.cloud.business.mapper.OrderMapper;
import com.cloud.business.service.ProductService;
import com.cloud.business.service.impl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Component
public class OrderTimeoutConsumer {

    private final OrderMapper orderMapper;
    private final ProductService productService;
    private final OrderServiceImpl orderService;

    //spring默认把交换机和消息队列都设置成持久化declare = "true"就是冗余的
    //接收过期后的信息
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "order.timeout.queue"), exchange = @Exchange(name = "order.direct"), key = "order.timeout"))
    public void handleTimeoutMessage(OrderPayMessageDTO message) {
        //查询订单
        OrderEntity order = orderMapper.findByOrderNo(message.getOrderNo());
        if (order == null) {
            return;
        }
        //是否支付成功
        if (order.getStatus().equals(OrderStatusEnum.PAID.getCode())) {
            return;
        }
        //回复库存
        boolean update = orderService.lambdaUpdate()
                .eq(OrderEntity::getOrderNo, order.getOrderNo())
                .eq(OrderEntity::getStatus, OrderStatusEnum.CREATED.getCode())
                .set(OrderEntity::getStatus, OrderStatusEnum.TIMEOUT.getCode())
                .update();
        if (update) {
            productService.restoreStock(order.getProductCode(), Long.valueOf(order.getQuantity()));
        }

    }

}
