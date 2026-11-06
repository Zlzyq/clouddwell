package com.cloud.business.persistence;

import com.cloud.business.entity.OrderEntity;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 演示环境订单不落库。
 * <p>生产环境请替换为 MyBatis Mapper + JDBC，并在此处删除本实现。</p>
 */
// @Repository  // 已切换为 DbOrderPersistence（MyBatis-Plus）
public class DemoMemoryOrderPersistence implements OrderPersistence {

    private static final AtomicLong ID_SEQUENCE = new AtomicLong(1000);

    private final ConcurrentHashMap<Long, OrderEntity> store = new ConcurrentHashMap<>();

    @Override
    public void insert(OrderEntity entity) {
        Long id = ID_SEQUENCE.getAndIncrement();
        entity.setOrderId(id);
        store.put(id, entity);
    }

}
