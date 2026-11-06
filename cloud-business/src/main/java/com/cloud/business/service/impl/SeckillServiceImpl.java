package com.cloud.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.business.common.enums.OrderStatusEnum;
import com.cloud.business.dto.SeckillSaveDTO;
import com.cloud.business.entity.OrderEntity;
import com.cloud.business.entity.SeckillEntity;
import com.cloud.business.mapper.SeckillMapper;
import com.cloud.business.persistence.OrderPersistence;
import com.cloud.business.service.SeckillService;
import com.cloud.business.vo.SeckillVO;
import com.cloud.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 秒杀活动表 服务实现类
 * </p>
 *
 * @author 张德帅
 * @since 2026-07-10
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SeckillServiceImpl extends ServiceImpl<SeckillMapper, SeckillEntity> implements SeckillService {

    private final StringRedisTemplate redisTemplate;
    private final TransactionTemplate template;
    private final OrderPersistence orderPersistence;
    private final OrderAsyncNotifyService orderAsyncNotifyService;

    private final DefaultRedisScript<Long> seckillRedisScript = new DefaultRedisScript<>();

    @EventListener(ApplicationReadyEvent.class)
    public void initSeckillStockToRedis() {
        seckillRedisScript.setLocation(new ClassPathResource("lua/seckill.lua"));
        seckillRedisScript.setResultType(Long.class);

        // 查询所有启用中的秒杀活动
        List<SeckillEntity> list = lambdaQuery()
                .eq(SeckillEntity::getStatus, 1)
                .list();
        Map<String, String> stockMap = new HashMap<>();
        for (SeckillEntity seckill : list) {
            stockMap.put("seckill:stock:" + seckill.getId(), String.valueOf(seckill.getSeckillStock()));
        }
        if (!stockMap.isEmpty()) {
            redisTemplate.opsForValue().multiSet(stockMap);
        }
        log.info("秒杀库存已加载到 Redis，共 {} 条", list.size());
    }

    @Override
    public List<SeckillVO> listAll() {
        List<SeckillEntity> list = lambdaQuery().list();
        return list.stream().map(seckillEntity -> {
            SeckillVO seckillVO = BeanUtil.copyProperties(seckillEntity, SeckillVO.class);
            seckillVO.setSoldCount(seckillEntity.getOriginalStock() - seckillEntity.getSeckillStock());
            return seckillVO;
        }).collect(Collectors.toList());
    }

    @Override
    public SeckillVO create(SeckillSaveDTO dto) {
        SeckillEntity seckillEntity = BeanUtil.copyProperties(dto, SeckillEntity.class);
        seckillEntity.setOriginalStock(dto.getSeckillStock());
        save(seckillEntity);
        SeckillVO vo = BeanUtil.copyProperties(seckillEntity, SeckillVO.class);
        vo.setSoldCount(0);
        return vo;
    }

    @Override
    public SeckillVO update(Long id, SeckillSaveDTO dto) {
        SeckillEntity byId = getById(id);
        if (byId == null) {
            throw new GlobalException("秒杀活动不存在");
        }
        // 把 DTO 的属性复制到已有的 entity 上，不会覆盖 originalStock
        BeanUtil.copyProperties(dto, byId);
        byId.setId(id);
        updateById(byId);
        SeckillVO vo = BeanUtil.copyProperties(byId, SeckillVO.class);
        vo.setSoldCount(byId.getOriginalStock() - byId.getSeckillStock());
        return vo;
    }

    @Override
    public void remove(Long id) {
        SeckillEntity entity = getById(id);
        if (entity == null) {
            throw new GlobalException("秒杀活动不存在");
        }
        removeById(id);
    }

    @Override
    public SeckillVO getByIdMe(Long id) {
        SeckillEntity entity = getById(id);
        if (entity == null) {
            throw new GlobalException("秒杀活动不存在");
        }
        SeckillVO vo = BeanUtil.copyProperties(entity, SeckillVO.class);
        vo.setSoldCount(entity.getOriginalStock() - entity.getSeckillStock());
        return vo;
    }

    @Override
    @Transactional
    public void seckillOrder(Long seckillId, Long userId, Integer quantity) {
        // ① 查秒杀活动
        SeckillEntity seckill = getById(seckillId);
        if (seckill == null) {
            throw new GlobalException("秒杀活动不存在");
        }

        // ② 校验活动状态和时间
        if (seckill.getStatus() != 1) {
            throw new GlobalException("秒杀活动未开启");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(seckill.getStartTime())) {
            throw new GlobalException("秒杀还没开始哦～");
        }
        if (now.isAfter(seckill.getEndTime())) {
            throw new GlobalException("秒杀已经结束啦～");
        }

        // ③ 校验限购数量
        if (quantity > seckill.getLimitCount()) {
            throw new GlobalException("每人限购" + seckill.getLimitCount() + "件");
        }

        // ④ 执行 Lua 脚本：扣库存 + 一人一单
        String stockKey = "seckill:stock:" + seckillId;
        String boughtKey = "seckill:bought:" + seckillId;
        Long result = redisTemplate.execute(seckillRedisScript,
                ListUtil.of(stockKey, boughtKey),
                userId.toString(), quantity.toString());

        if (result == null || result == 0L) {
            throw new GlobalException("你已经参与过这个秒杀啦～");
        }
        if (result == 1L) {
            throw new GlobalException("库存不足～");
        }

        // ⑤ 写订单（用秒杀价）+ MQ 异步落 MySQL 库存
        try {
            template.execute(status -> {
                String orderNo = genOrderNo();
                OrderEntity order = new OrderEntity();
                order.setOrderNo(orderNo);
                order.setBuyerUserId(userId);
                order.setProductCode(seckill.getProductCode());
                order.setQuantity(quantity);
                order.setTotalAmount(seckill.getSeckillPrice().multiply(BigDecimal.valueOf(quantity)));
                order.setStatus(OrderStatusEnum.CREATED.getCode());
                orderPersistence.insert(order);
                return null;
            });
            // MQ 异步扣 MySQL 秒杀库存
            orderAsyncNotifyService.notifySeckillStockDeduct(seckillId, Long.valueOf(quantity));
        } catch (Exception e) {
            redisTemplate.opsForValue().increment(stockKey, quantity);
            redisTemplate.opsForSet().remove(boughtKey, userId.toString());
            throw new GlobalException("下单失败，已恢复库存");
        }
    }

    private static String genOrderNo() {
        return "SK" + IdUtil.getSnowflake(1, 1).nextIdStr();
    }

}
