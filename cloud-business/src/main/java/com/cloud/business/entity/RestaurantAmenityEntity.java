package com.cloud.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 饭店待遇表 — 用 MySQL 存储，用于 ES 搜索演示
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("restaurant_amenity")
public class RestaurantAmenityEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 饭店名称
     */
    private String restaurantName;

    /**
     * 地址
     */
    private String address;

    /**
     * 电话
     */
    private String phone;

    /**
     * 评分 1-5
     */
    private Integer rating;

    /**
     * 菜系类型（川菜、粤菜、日料等）
     */
    private String cuisineType;

    /**
     * 描述
     */
    private String description;

    /**
     * 特色标签，逗号分隔
     */
    private String amenities;

    /**
     * 状态 1=启用 0=停用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 纬度（lat）
     */
    private Double latitude;
    /**
     * 经度（lon）
     */
    private Double longitude;

    /**
     * ES 搜索算分（仅用于二次过滤排序，不入库）
     */
    @TableField(exist = false)
    private Double score;
}
