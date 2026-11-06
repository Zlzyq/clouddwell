package com.cloud.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.business.entity.HomeServiceProviderEntity;
import com.cloud.business.mapper.HomeServiceProviderMapper;
import com.cloud.business.service.HomeServiceProviderService;
import com.cloud.common.domain.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 人工上门 — 服务者 服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HomeServiceProviderServiceImpl extends ServiceImpl<HomeServiceProviderMapper, HomeServiceProviderEntity>
        implements HomeServiceProviderService {

    private static final String[] NAMES = {
            "张师傅", "李师傅", "王师傅", "赵师傅", "刘师傅",
            "陈师傅", "杨师傅", "黄师傅", "周师傅", "吴师傅",
            "孙师傅", "马师傅", "朱师傅", "胡师傅", "郭师傅"
    };

    private static final String[][] SKILLS_SET = {
            {"清洁", "打扫", "收纳"},
            {"维修", "电工", "管道"},
            {"烹饪", "家常菜", "面点"},
            {"搬家", "搬运", "装卸"},
            {"清洁", "烹饪", "收纳"},
            {"维修", "安装", "家电"},
            {"烹饪", "烘焙", "西餐"},
            {"清洁", "搬家", "搬运"},
            {"维修", "木工", "油漆"},
            {"护理", "按摩", "理疗"},
            {"清洁", "烹饪", "维修"},
            {"家教", "辅导", "英语"},
            {"园艺", "修剪", "绿化"},
            {"宠物", "遛狗", "喂养"},
            {"摄影", "摄像", "修图"}
    };

    private static final double[][] LOCATIONS = {
            {39.9042, 116.4074},
            {39.9142, 116.4174},
            {39.8942, 116.3974},
            {39.9242, 116.4274},
            {39.8842, 116.3874},
            {39.9342, 116.4374},
            {39.8742, 116.3774},
            {39.9442, 116.4474},
            {39.8642, 116.3674},
            {39.9542, 116.4574},
            {39.8442, 116.3574},
            {39.9642, 116.4674},
            {39.8342, 116.3474},
            {39.9742, 116.4774},
            {39.8242, 116.3374}
    };

    @Override
    public PageResult<HomeServiceProviderEntity> listAll(int pageNo, int pageSize) {
        Page<HomeServiceProviderEntity> page = Page.of(pageNo, pageSize);
        Page<HomeServiceProviderEntity> result = baseMapper.selectPage(page, null);
        return new PageResult<>(pageNo, pageSize, result.getTotal(), result.getRecords(), null);
    }

    @Override
    public String initTestData(int count) {
        Random random = new Random();
        List<HomeServiceProviderEntity> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int idx = i % NAMES.length;
            String[] skillsArr = SKILLS_SET[idx];
            double[] loc = LOCATIONS[idx];

            HomeServiceProviderEntity entity = new HomeServiceProviderEntity()
                    .setName(NAMES[idx] + (i >= NAMES.length ? "_" + (i / NAMES.length) : ""))
                    .setPhone("138" + String.format("%08d", 10000000 + i))
                    .setAvatar("/img/avatar-1.png")
                    .setSkills(String.join(",", skillsArr))
                    .setLatitude(loc[0] + (random.nextDouble() - 0.5) * 0.02)
                    .setLongitude(loc[1] + (random.nextDouble() - 0.5) * 0.02)
                    .setRating(Math.round((3.0 + random.nextDouble() * 2.0) * 10) / 10.0)
                    .setAcceptRate(Math.round((60.0 + random.nextDouble() * 40.0) * 10) / 10.0)
                    .setOrderCount(10 + random.nextInt(200))
                    .setStatus(random.nextInt(3))
                    .setServiceRadius(5 + random.nextInt(16))
                    .setCreateTime(LocalDateTime.now())
                    .setUpdateTime(LocalDateTime.now());
            list.add(entity);
        }
        saveBatch(list, 500);
        log.info("生成了 {} 条上门服务者测试数据", count);
        return "已生成 " + count + " 条上门服务者数据";
    }

    @Override
    public void clearTestData() {
        LambdaQueryWrapper<HomeServiceProviderEntity> wrapper = new LambdaQueryWrapper<HomeServiceProviderEntity>()
                .likeRight(HomeServiceProviderEntity::getName, "压测-");
        baseMapper.delete(wrapper);
        log.info("已清空上门服务者测试数据");
    }
}
