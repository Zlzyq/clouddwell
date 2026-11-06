package com.cloud.business.persistence;

import com.cloud.business.entity.OrderEntity;

/**
 * 订单持久化扩展点占位。
 * <p>演示默认由 {@link DemoMemoryOrderPersistence} 内存存储；接入 MySQL 时可改为调用 MyBatis-Plus Mapper（如重构原 {@code OrderMapper}）。</p>
 */
public interface OrderPersistence {

    void insert(OrderEntity entity);
}
