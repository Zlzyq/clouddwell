package com.cloud.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.business.entity.RestaurantAmenityEntity;
import com.cloud.common.domain.PageResult;

import java.util.List;

/**
 * 饭店待遇 服务接口
 */
public interface RestaurantAmenityService extends IService<RestaurantAmenityEntity> {

    /**
     * 分页查询
     */
    PageResult<RestaurantAmenityEntity> listAll(int pageNo, int pageSize);

    /**
     * 搜索（按饭店名称、地址、菜系模糊匹配）
     */
    PageResult<RestaurantAmenityEntity> search(String keyword, int pageNo, int pageSize);

    /**
     * 搜索（右模糊 LIKE 'keyword%'，可利用 B+ 树索引前缀跳转）
     */
    PageResult<RestaurantAmenityEntity> searchRightLike(String keyword, int pageNo, int pageSize);

    /**
     * 按名称搜索（测试用）
     */
    List<RestaurantAmenityEntity> getByName(String name);

    /**
     * 批量删除
     */
    void removeBatch(List<Long> ids);

    /**
     * 生成测试数据
     */
    String initTestData(int count);

    /**
     * 清空测试数据（restaurant_name 以"压测-"开头的）
     */
    void clearTestData();
}
