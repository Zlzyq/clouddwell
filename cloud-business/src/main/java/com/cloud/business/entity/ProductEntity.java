package com.cloud.business.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/** 产品表 biz_product */
@TableName("biz_product")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;
    private String code;
    private String name;
    private String category;
    private String price;
    private Integer stock;
    private String status;

}
