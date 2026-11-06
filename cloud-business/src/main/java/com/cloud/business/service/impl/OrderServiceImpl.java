package com.cloud.business.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.business.common.enums.OrderStatusEnum;
import com.cloud.business.converter.OrderConverter;
import com.cloud.business.dto.SubmitOrderDTO;
import com.cloud.business.entity.OrderEntity;
import com.cloud.business.feign.dto.UserRemoteVO;
import com.cloud.business.handler.OrderHandler;
import com.cloud.business.mapper.OrderMapper;
import com.cloud.business.persistence.OrderPersistence;
import com.cloud.business.service.AdminOrderService;
import com.cloud.business.service.OrderService;
import com.cloud.business.service.ProductService;
import com.cloud.business.vo.OrderIntegrationResult;
import com.cloud.business.vo.OrderVO;
import com.cloud.business.vo.ProductVO;
import com.cloud.common.constant.MdcKeys;
import com.cloud.common.context.UserContext;
import com.cloud.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderHandler orderHandler;
    private final OrderConverter orderConverter;
    private final OrderPersistence orderPersistence;
    private final OrderAsyncNotifyService orderAsyncNotifyService;
    private final ProductService productService;
    private final StringRedisTemplate redisTemplate;
    private final TransactionTemplate template;
    private final AdminOrderService adminOrderService;
    /**
     * 雪花算法：生成全局唯一订单号
     */
    //static是属于类本身的 直接调用类 默认就是这个
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);

    @Override
    public OrderVO submitOrder(SubmitOrderDTO dto) {
        //获取当前用户信息
        Long ctxUid = UserContext.getUid();
        if (ctxUid == null) {
            throw new GlobalException("请先登录后再下单");
        }
        if (!ctxUid.equals(dto.getBuyerUserId())) {
            throw new GlobalException("登录用户与下单用户不一致");
        }
        //获取订单信息
        OrderIntegrationResult remoteBundle = orderHandler.execute(dto);
        UserRemoteVO user = remoteBundle.getUser();
        if (user == null || !user.getUserId().equals(dto.getBuyerUserId())) {
            throw new GlobalException("用户信息校验失败");
        }
        //（压测时注释掉）在redis加入key防止重复下单
        String key = "order:deduplication" + ":" + ctxUid + ":" + remoteBundle.getProduct().getProductCode();
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(key, "1", 5, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(setIfAbsent)) {
            throw new GlobalException("请勿重复下单");
        }
        //扣库存（锁商品行 和取消/退款拿锁顺序一致，防死锁）此时扣的是redis的 不用担心发生死锁
        Boolean aBoolean = productService.deDuctStockByRedis(dto.getProductCode(), Long.valueOf(dto.getQuantity()));
        if (!aBoolean) {
            throw new GlobalException("库存不足扣减失败");
        }
        try {
            OrderVO orderVO = template.execute(status -> {
                //先算价格和生成订单号
                BigDecimal total = orderConverter.calculateTotal(remoteBundle.getProduct(), dto.getQuantity());
                String orderNo = genOrderNo();
                OrderEntity entity = orderConverter.toNewEntity(orderNo, dto, remoteBundle.getProduct(), OrderStatusEnum.CREATED.getCode(), total);
                //先写订单（锁订单行）
                orderPersistence.insert(entity);
                String traceId = MDC.get(MdcKeys.TRACE_ID);
                //生产者 发送超时消息（支付按钮单独触发）
                orderAsyncNotifyService.notifyOrderTimeout(orderNo, dto.getBuyerUserId(), traceId);
                //同步到管理订单列表
                try {
                    ProductVO productVO = productService.getByCode(dto.getProductCode());
                    adminOrderService.syncFromRealOrder(orderNo, user.getUserName(), dto.getProductCode(), productVO != null ? productVO.getName() : dto.getProductCode(), dto.getQuantity(), total.toString(), OrderStatusEnum.CREATED.name(), DateUtil.now());
                } catch (Exception e) {
                    log.warn("同步到管理订单列表失败", e);
                }
                return orderConverter.convert(entity);
            });
            //发送异步转移数据库的消息
            orderAsyncNotifyService.notifyStockDeduct(dto.getProductCode(), Long.valueOf(dto.getQuantity()));
            return orderVO;
        } catch (Exception e) {
            productService.restoreStockByRedis(dto.getProductCode(), Long.valueOf(dto.getQuantity()));
            throw e;
        }


    }


    /**
     * 取消订单
     */
    @Override
    public void cancelOrder(Long userId, String orderNo) {

        //获取订单信息
        OrderEntity orderEntity = lambdaQuery().eq(OrderEntity::getOrderNo, orderNo).one();
        if (orderEntity == null) {
            throw new GlobalException("订单不存在");
        }
        //获取当前用户信息
        Long ctxUid = UserContext.getUid();
        if (ctxUid == null) {
            throw new GlobalException("请先登录后再下单");
        }
        if (!ctxUid.equals(orderEntity.getBuyerUserId())) {
            throw new GlobalException("登录用户与下单用户不一致");
        }
        //检查状态
        boolean equals = orderEntity.getStatus().equals(OrderStatusEnum.CREATED.getCode());
        if (!equals) {
            throw new GlobalException("订单无法取消");
        }
        //修改状态为"已取消"
        boolean update = lambdaUpdate().eq(OrderEntity::getOrderNo, orderNo).eq(OrderEntity::getStatus, OrderStatusEnum.CREATED.getCode())  // 只取消未支付的
                .set(OrderEntity::getStatus, OrderStatusEnum.CANCELLED.getCode()).update();
        if (!update) {
            throw new GlobalException("取消失败，订单不存在或状态异常");
        }
        //恢复库存(需要商品编号和回复的数量)
        Boolean aBoolean = productService.restoreStockByRedis(orderEntity.getProductCode(), Long.valueOf(orderEntity.getQuantity()));
        if (!aBoolean) {
            throw new GlobalException("恢复失败，订单不存在或状态异常");
        }
        //删除重复下单的key
        String key = "order:deduplication" + ":" + ctxUid + ":" + orderEntity.getProductCode();
        redisTemplate.delete(key);
        //异步同步管理订单状态 重要的先发 不重要的后发
        orderAsyncNotifyService.notifyStockRestore(orderEntity.getProductCode(), Long.valueOf(orderEntity.getQuantity()));
        orderAsyncNotifyService.notifyOrderStatusSync(orderNo, "CANCELLED");
    }

    private static String genOrderNo() {
        return "ORD" + SNOWFLAKE.nextIdStr();
    }

    /**
     * 退款
     */
    @Override
    public void refund(Long userId, String orderNo) {
        OrderEntity order = lambdaQuery().eq(OrderEntity::getOrderNo, orderNo).one();
        if (order == null) {
            throw new GlobalException("订单不存在");
        }
        //获取当前用户信息
        Long ctxUid = UserContext.getUid();
        if (ctxUid == null) {
            throw new GlobalException("请先登录后再下单");
        }
        if (!ctxUid.equals(order.getBuyerUserId())) {
            throw new GlobalException("登录用户与下单用户不一致");
        }
        if (!order.getStatus().equals(OrderStatusEnum.PAID.getCode())) {
            throw new GlobalException("只有已支付才能退款");
        }
        boolean update = lambdaUpdate().eq(OrderEntity::getOrderNo, orderNo).eq(OrderEntity::getStatus, OrderStatusEnum.PAID.getCode()).set(OrderEntity::getStatus, OrderStatusEnum.REFUNDED.getCode()).update();
        if (!update) {
            throw new GlobalException("出错了请重试");
        }
        Boolean aBoolean = productService.restoreStockByRedis(order.getProductCode(), Long.valueOf(order.getQuantity()));
        if (!aBoolean) {
            throw new GlobalException("退款失败，订单不存在或状态异常");
        }
        String key = "order:deduplication" + ":" + ctxUid + ":" + order.getProductCode();
        redisTemplate.delete(key);
        //异步同步管理订单状态
        orderAsyncNotifyService.notifyStockRestore(order.getProductCode(), Long.valueOf(order.getQuantity()));
        orderAsyncNotifyService.notifyOrderStatusSync(orderNo, "REFUNDED");
    }

    //线程的id通过mq就没了
    @Override
    public void payOrder(String orderNo, Long userId) {
        boolean update = lambdaUpdate()
                .eq(OrderEntity::getOrderNo, orderNo)
                .eq(OrderEntity::getBuyerUserId, userId)
                .eq(OrderEntity::getStatus, OrderStatusEnum.CREATED.getCode())
                .set(OrderEntity::getStatus, OrderStatusEnum.PAID.getCode()).update();
        if (!update) {
            throw new GlobalException("支付失败");
        }
        //同步管理订单状态
        adminOrderService.syncStatus(orderNo, "PAID");
    }
}
