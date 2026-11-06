package com.cloud.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 人工上门 — 服务者表
 * <p>
 * 技能标签（skills）用逗号分隔，如 "清洁,维修,烹饪,搬家"
 * 接单率（accept_rate）范围 0~100
 * 经纬度用于 ES geo_distance 圈人
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("home_service_provider")
public class HomeServiceProviderEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 姓名 */
    private String name;

    /** 手机号 */
    private String phone;

    /** 头像 URL */
    private String avatar;

    /** 技能标签，逗号分隔：清洁,维修,烹饪,搬家 */
    private String skills;

    /** 纬度 */
    private Double latitude;

    /** 经度 */
    private Double longitude;

    /** 评分 1~5 */
    private Double rating;

    /** 接单率 0~100 */
    private Double acceptRate;

    /** 历史接单数 */
    private Integer orderCount;

    /** 状态：0=离线 1=在线 2=忙碌 */
    private Integer status;

    /** 服务半径（公里） */
    private Integer serviceRadius;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
