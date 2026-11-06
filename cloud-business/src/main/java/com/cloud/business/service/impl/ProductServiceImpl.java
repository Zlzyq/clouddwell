package com.cloud.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.business.converter.ProductConverter;
import com.cloud.business.dto.ProductSaveDTO;
import com.cloud.business.entity.ProductEntity;
import com.cloud.business.mapper.ProductMapper;
import com.cloud.business.service.ProductService;
import com.cloud.business.vo.ProductVO;
import com.cloud.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, ProductEntity> implements ProductService {

    private final ProductConverter productConverter;
    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Boolean> deDuctStockRedis = new DefaultRedisScript<>();
    private final DefaultRedisScript<Boolean> restoreRedisScript = new DefaultRedisScript<>();

    @EventListener(ApplicationReadyEvent.class)
    public void redisScriptConfig() {
        deDuctStockRedis.setLocation(new ClassPathResource("lua/deduct_stock.lua"));
        deDuctStockRedis.setResultType(Boolean.class);

        restoreRedisScript.setLocation(new ClassPathResource("lua/restore_stock.lua"));
        restoreRedisScript.setResultType(Boolean.class);

        // 加载 MySQL 库存到 Redis
        List<ProductEntity> list = list();
        //List MyBatisPlus 的方法 查 MySQL 里所有商品
        Map<String, String> stockMap = new HashMap<>();
        for (ProductEntity product : list) {
            stockMap.put("stock:product:" + product.getCode(), String.valueOf(product.getStock()));
        }
        redisTemplate.opsForValue().multiSet(stockMap);
        log.info("商品库存已加载到 Redis，共 {} 条", list.size());
    }


    @Override
    public List<ProductVO> listAll() {
        return list().stream().map(entity -> BeanUtil.copyProperties(entity, ProductVO.class)).collect(Collectors.toList());

    }

    @Override
    public ProductVO getById(Long id) {
        ProductEntity entity = super.getById(id);
        if (entity == null) throw new GlobalException("记录不存在");
        return productConverter.convert(entity);
    }

    @Override
    public ProductVO create(ProductSaveDTO dto) {
        ProductEntity entity = productConverter.toNewEntity(dto);
        save(entity);
        return productConverter.convert(entity);
    }

    @Override
    public ProductVO update(Long id, ProductSaveDTO dto) {
        ProductEntity entity = super.getById(id);
        if (entity == null) throw new GlobalException("记录不存在");
        productConverter.applyDto(entity, dto);
        entity.setId(id);
        updateById(entity);
        return productConverter.convert(entity);
    }

    @Override
    public void remove(Long id) {
        removeById(id);
    }

    @Override
    public List<ProductVO> reset() {
        return List.of();
    }

    @Override
    public ProductVO getByCode(String code) {
        ProductEntity one = lambdaQuery()
                .eq(ProductEntity::getCode, code)
                .one();
        return BeanUtil.copyProperties(one, ProductVO.class);

    }

    @Override
    public Boolean deductStock(String code, Long quantity) {
        return update(new LambdaUpdateWrapper<ProductEntity>()
                .setSql("stock = stock - " + quantity)
                .eq(ProductEntity::getCode, code)
                .ge(ProductEntity::getStock, quantity));
    }

    @Override
    public Boolean restoreStock(String code, Long quantity) {
        return update(new LambdaUpdateWrapper<ProductEntity>()
                .setSql("stock = stock + " + quantity)
                .eq(ProductEntity::getCode, code));
    }

    @Override
    public Boolean deDuctStockByRedis(String code, Long quantity) {
        String key = "stock:product:" + code;
        Boolean execute = redisTemplate.execute(deDuctStockRedis, ListUtil.of(key), quantity.toString());
        if (Boolean.FALSE.equals(execute) || execute == null) {
            throw new GlobalException("库存不足扣减失败");
        }
        return execute;
    }

    @Override
    public Boolean restoreStockByRedis(String code, Long quantity) {
        String key = "stock:product:" + code;
        Boolean execute = redisTemplate.execute(restoreRedisScript, ListUtil.of(key), quantity.toString());
        if (Boolean.FALSE.equals(execute) || execute == null) {
            throw new GlobalException("恢复库存失败");
        }
        return execute;
    }
}
