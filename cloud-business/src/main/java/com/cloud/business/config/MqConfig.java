package com.cloud.business.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MqConfig {

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    //声明延时队列
    @Bean
    public Queue orderDelayQueue() {
        //durable持久的重启不会清除
        return QueueBuilder.durable("order.delay.queue")
                // 消息过期之后 发给 order.direct
                .withArgument("x-dead-letter-exchange", "order.direct")
                //消息过期后用 order.timeout 这个路由 key 发出去
                .withArgument("x-dead-letter-routing-key", "order.timeout")
                //消息在这个队列里待 30 分钟就过期
                .withArgument("x-message-ttl", 30 * 60 * 1000)
                .build();
    }

    @Bean
    public Binding orderDelayBinding() {
        return BindingBuilder.bind(orderDelayQueue())
                .to(new DirectExchange("order.direct"))
                .with("order.pay.timeout");
    }


}
