package com.cloud.business.handler;

import cn.hutool.json.JSONUtil;
import com.cloud.business.entity.OrderEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class HuToolTest {
    @Test
    public static void main(String[] args) {
        OrderEntity entity = new OrderEntity();
        entity.setOrderNo("TEST123");
        entity.setCreateTime(LocalDateTime.now());

        String json = JSONUtil.toJsonStr(entity);
        System.out.println("存进去：" + json);

        OrderEntity back = JSONUtil.toBean(json, OrderEntity.class);
        System.out.println("取出来：" + back.getCreateTime());
    }
}
