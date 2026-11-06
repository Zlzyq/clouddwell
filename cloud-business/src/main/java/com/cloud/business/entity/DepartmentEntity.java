package com.cloud.business.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 部门表 biz_department
 */
@TableName("biz_department")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;
    private String companyName;
    private String name;
    private String leader;
    private String phone;
    private String status;

}
