package com.cloud.business.converter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.cloud.business.dto.SubmitOrderDTO;
import com.cloud.business.entity.OrderEntity;
import com.cloud.business.feign.dto.ProductRemoteVO;
import com.cloud.business.vo.OrderVO;
import com.cloud.common.base.BaseConverter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Component
public class OrderConverter extends BaseConverter<OrderEntity, OrderVO> {

    @Override
    public OrderVO convert(OrderEntity source) {
        if (source == null) {
            return null;
        }
        OrderVO vo = new OrderVO();
        BeanUtil.copyProperties(source, vo);
        vo.setCreateTimeDisplay(source.getCreateTime() != null
                ? LocalDateTimeUtil.formatNormal(source.getCreateTime()) : null);
        return vo;
    }

    public OrderEntity toNewEntity(String orderNo, SubmitOrderDTO dto, ProductRemoteVO pricing,
                                   int orderStatus, BigDecimal totalAmount) {
        LocalDateTime now = LocalDateTime.now();
        OrderEntity entity = new OrderEntity();
        entity.setOrderNo(orderNo);
        entity.setBuyerUserId(dto.getBuyerUserId());
        entity.setProductCode(dto.getProductCode().trim());
        entity.setQuantity(dto.getQuantity());
        entity.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
        entity.setStatus(orderStatus);
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        return entity;
    }

    /** 明细金额计算：单价 * 数量（四舍五入） */
    public BigDecimal calculateTotal(ProductRemoteVO product, int quantity) {
        return product.getUnitPrice()
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
