package com.cloud.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 秒杀活动表
 * </p>
 *
 * @author 张德帅
 * @since 2026-07-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("biz_seckill")
public class SeckillEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 秒杀活动名称
     */
    private String seckillName;

    /**
     * 关联商品编码
     */
    private String productCode;

    /**
     * 秒杀价
     */
    private BigDecimal seckillPrice;

    /**
     * 秒杀剩余库存
     */
    private Integer seckillStock;

    /**
     * 原始库存
     */
    private Integer originalStock;

    /**
     * 每人限购数量
     */
    private Integer limitCount;

    /**
     * 秒杀开始时间
     */
    private LocalDateTime startTime;

    /**
     * 秒杀结束时间
     */
    private LocalDateTime endTime;

    /**
     * 状态 0=未发布 1=进行中 2=已结束
     */
    private Integer status;

    /**
     * 乐观锁
     */
    private Integer version;

    /**
     * 逻辑删除 0=正常 1=删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
