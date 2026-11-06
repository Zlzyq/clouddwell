package com.cloud.business.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MqConfirmConfig {


    public MqConfirmConfig(RabbitTemplate rabbitTemplate) {
        // 构造器注入，不用自己去容器里拿
        rabbitTemplate.setReturnsCallback(returned -> log.warn("路由失败：{}", returned.getMessage()));
    }
}
