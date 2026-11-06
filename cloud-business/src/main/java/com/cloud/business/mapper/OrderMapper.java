package com.cloud.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.business.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {

    /** 根据订单号查询订单 */
    @Select("SELECT * FROM biz_order WHERE order_no = #{orderNo}")
    OrderEntity findByOrderNo(String orderNo);
}
