package com.cloud.business.persistence;

import com.cloud.business.entity.OrderEntity;
import com.cloud.business.mapper.OrderMapper;
import org.springframework.stereotype.Repository;

/**
 * 订单数据库持久化（MyBatis-Plus 实现）。
 * <p>替换 {@link DemoMemoryOrderPersistence} 的内存存储。</p>
 */
@Repository
public class DbOrderPersistence implements OrderPersistence {

    private final OrderMapper orderMapper;

    public DbOrderPersistence(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public void insert(OrderEntity entity) {
        orderMapper.insert(entity);
    }
}
