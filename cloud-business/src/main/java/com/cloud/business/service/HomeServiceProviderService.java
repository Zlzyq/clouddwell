package com.cloud.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.business.entity.HomeServiceProviderEntity;
import com.cloud.common.domain.PageResult;

/**
 * 人工上门 — 服务者 服务接口
 */
public interface HomeServiceProviderService extends IService<HomeServiceProviderEntity> {

    /**
     * 分页查询
     */
    PageResult<HomeServiceProviderEntity> listAll(int pageNo, int pageSize);

    /**
     * 生成测试数据
     */
    String initTestData(int count);

    /**
     * 清空测试数据（name 以"压测-"开头的）
     */
    void clearTestData();
}
