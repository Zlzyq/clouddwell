package com.cloud.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.business.converter.AdminOrderConverter;
import com.cloud.business.dto.AdminOrderSaveDTO;
import com.cloud.business.entity.AdminOrderEntity;
import com.cloud.business.mapper.AdminOrderMapper;
import com.cloud.business.service.AdminOrderService;
import com.cloud.business.service.ProductService;
import com.cloud.business.vo.AdminOrderVO;
import com.cloud.common.domain.PageResult;
import com.cloud.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl extends ServiceImpl<AdminOrderMapper, AdminOrderEntity> implements AdminOrderService {

    private final AdminOrderMapper adminOrderMapper;
    private final AdminOrderConverter adminOrderConverter;
    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;
    private final ProductService productService;


    @Override
    public List<AdminOrderVO> listAll() {
        String key = "Admin:Order:listAll";
        String s = redisTemplate.opsForValue().get(key);
        if (s != null) {
            if (s.isEmpty()) {
                throw new GlobalException("没有这个订单");
            }
            return JSONUtil.toList(s, AdminOrderVO.class);
        }
        //没有从数据库拿
        List<AdminOrderEntity> listPo = query().list();
        if (listPo.isEmpty()) {
            redisTemplate.opsForValue().set(key, "", 600 + RandomUtil.randomInt(5), TimeUnit.SECONDS);
            throw new GlobalException("没有这个订单");
        }
        //存入redis
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(listPo), 600 + RandomUtil.randomInt(5), TimeUnit.SECONDS);
        return BeanUtil.copyToList(listPo, AdminOrderVO.class);

    }

    @Override
    public AdminOrderVO getById(Long id) {
        String key = "Admin:Order:" + id;
        String lockName = "Admin:Order:lock:" + id;
        String s = redisTemplate.opsForValue().get(key);
        if (s != null) {
            if (s.isEmpty()) {
                throw new GlobalException("订单不存在");
            }
            return JSONUtil.toBean(s, AdminOrderVO.class);
        }

        //没有从数据库拿(加入分布式锁)
        RLock lock = redissonClient.getLock(lockName);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            //双重检查
            String ss = redisTemplate.opsForValue().get(key);
            if (ss != null) {
                return JSONUtil.toBean(ss, AdminOrderVO.class);
            }
            AdminOrderEntity po = query().eq("id", id).one();
            //还是没有的话返回null控制防治穿透
            if (po == null) {
                redisTemplate.opsForValue().set(key, "", 600 + RandomUtil.randomInt(5), TimeUnit.SECONDS);
                throw new GlobalException("订单不存在");
            }

            //存redis
            redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(po), 600 + RandomUtil.randomInt(5), TimeUnit.SECONDS);
            return BeanUtil.toBean(po, AdminOrderVO.class);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    // 两个操作或者俩个以上 就要加事务
    public AdminOrderVO create(AdminOrderSaveDTO dto) {
        AdminOrderEntity entity = adminOrderConverter.toNewEntity(dto);
        Boolean aBoolean = productService.deductStock(entity.getProductCode(), Long.valueOf(entity.getQuantity()));
        if (!aBoolean) {
            throw new GlobalException("扣减失败，库存不足");
        }
        adminOrderMapper.insert(entity);
        //删除相关的所有Redis缓存
        redisTemplate.delete("Admin:Order:listAll");
        return adminOrderConverter.convert(entity);
    }

    @Override
    public AdminOrderVO update(Long id, AdminOrderSaveDTO dto) {
        AdminOrderEntity entity = adminOrderMapper.selectById(id);
        if (entity == null) return null;
        adminOrderConverter.applyDto(entity, dto);
        entity.setId(id);
        adminOrderMapper.updateById(entity);
        //删相关的所有Redis缓存
        redisTemplate.delete("Admin:Order:listAll");
        redisTemplate.delete("Admin:Order:" + id);
        return adminOrderConverter.convert(entity);
    }

    @Override
    public void remove(Long id) {
        AdminOrderEntity entity = adminOrderMapper.selectById(id);
        if (entity != null && !"CREATED".equals(entity.getStatus())) {
            throw new GlobalException("只能删除未支付的订单");
        }
        adminOrderMapper.deleteById(id);
        //删除相关的所有Redis缓存
        redisTemplate.delete("Admin:Order:listAll");
        redisTemplate.delete("Admin:Order:" + id);
    }

    @Override
    public List<AdminOrderVO> reset() {
        return listAll();
    }

    @Override
    public void batchDelete(List<Long> idList) {
        //删列表
        removeByIds(idList);
        //删缓存
        redisTemplate.delete("Admin:Order:listAll");
        idList.forEach(id -> redisTemplate.delete("Admin:Order:" + id));
    }

    @Override
    public PageResult<AdminOrderVO> page(Integer pageNo, Integer pageSize) {
        Page<AdminOrderEntity> page = page(new Page<>(pageNo, pageSize));
        //接下来要转成 PageResult<AdminOrderVO> 返回
        PageResult<AdminOrderVO> result = new PageResult<>();
        result.setPageNo(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setTotal(page.getTotal());
        List<AdminOrderEntity> records = page.getRecords();
        result.setRecords(BeanUtil.copyToList(records, AdminOrderVO.class));
        return result;
    }

    @Override
    public void syncFromRealOrder(String orderNo, String buyerName, String productCode,
                                  String productName, Integer quantity, String amount,
                                  String status, String createTime) {
        AdminOrderEntity entity = new AdminOrderEntity();
        entity.setOrderNo(orderNo);
        entity.setBuyerName(buyerName);
        entity.setProductCode(productCode);
        entity.setProductName(productName == null || productName.isBlank() ? productCode : productName);
        entity.setQuantity(quantity);
        entity.setAmount(amount);
        entity.setStatus(status);
        entity.setCreateTime(createTime);
        adminOrderMapper.insert(entity);
        // 删除列表缓存，下次查询就能看到新数据
        redisTemplate.delete("Admin:Order:listAll");
    }

    @Override
    public void syncStatus(String orderNo, String status) {
        boolean updated = update(new LambdaUpdateWrapper<AdminOrderEntity>()
                .eq(AdminOrderEntity::getOrderNo, orderNo)
                .set(AdminOrderEntity::getStatus, status));
        if (updated) {
            redisTemplate.delete("Admin:Order:listAll");
        }
    }

    @Override
    public List<AdminOrderVO> searchByName(String keyword) {
        String key = "Admin:Order:search:" + keyword;
        String cached = redisTemplate.opsForValue().get(key);
        // 判断是否为空
        if (cached != null) {
            return JSONUtil.toList(cached, AdminOrderVO.class);
        }

        // 加入分布式锁，防止击穿
        String lockName = "Admin:Order:search:lock:" + keyword;
        RLock lock = redissonClient.getLock(lockName);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            // 双重检查
            String ss = redisTemplate.opsForValue().get(key);
            if (ss != null) {
                return JSONUtil.toList(ss, AdminOrderVO.class);
            }

            List<AdminOrderEntity> list = lambdaQuery()
                    .eq(AdminOrderEntity::getBuyerName, keyword)
                    .list();
            List<AdminOrderVO> voList = BeanUtil.copyToList(list, AdminOrderVO.class);

            // 加入缓存随机时间
            redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(voList), 600 + RandomUtil.randomInt(5), TimeUnit.SECONDS);

            return voList;
        } finally {
            lock.unlock();
        }
    }
}
