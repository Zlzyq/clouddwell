package com.cloud.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.business.entity.RestaurantAmenityEntity;
import com.cloud.business.mapper.RestaurantAmenityMapper;
import com.cloud.business.service.RestaurantAmenityService;
import com.cloud.common.domain.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 饭店待遇 服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantAmenityServiceImpl extends ServiceImpl<RestaurantAmenityMapper, RestaurantAmenityEntity>
        implements RestaurantAmenityService {

    @Override
    public PageResult<RestaurantAmenityEntity> listAll(int pageNo, int pageSize) {
        Page<RestaurantAmenityEntity> page = page(new Page<>(pageNo, pageSize),
                new LambdaQueryWrapper<RestaurantAmenityEntity>().orderByDesc(RestaurantAmenityEntity::getId));
        return new PageResult<>(pageNo, pageSize, page.getTotal(), page.getRecords(), null);
    }

    @Override
    public PageResult<RestaurantAmenityEntity> search(String keyword, int pageNo, int pageSize) {
        if (keyword == null || keyword.isBlank()) {
            return listAll(pageNo, pageSize);
        }
        Page<RestaurantAmenityEntity> page = page(new Page<>(pageNo, pageSize),
                new LambdaQueryWrapper<RestaurantAmenityEntity>()
                        .like(RestaurantAmenityEntity::getRestaurantName, keyword)
                        //默认是and 切换成or的模式
                        .or()
                        .like(RestaurantAmenityEntity::getAddress, keyword)
                        .or()
                        .like(RestaurantAmenityEntity::getCuisineType, keyword)
                        .orderByDesc(RestaurantAmenityEntity::getId));
        return new PageResult<>(pageNo, pageSize, page.getTotal(), page.getRecords(), null);
    }

    @Override
    public PageResult<RestaurantAmenityEntity> searchRightLike(String keyword, int pageNo, int pageSize) {
        if (keyword == null || keyword.isBlank()) {
            return listAll(pageNo, pageSize);
        }
        // rightLike = LIKE 'keyword%'，可以利用 B+ 树索引前缀跳转
        Page<RestaurantAmenityEntity> page = page(new Page<>(pageNo, pageSize),
                new LambdaQueryWrapper<RestaurantAmenityEntity>()
                        .likeRight(RestaurantAmenityEntity::getRestaurantName, keyword)
                        //切换成or的模式
                        .or()
                        .likeRight(RestaurantAmenityEntity::getCuisineType, keyword)
                        .orderByDesc(RestaurantAmenityEntity::getId));
        return new PageResult<>(pageNo, pageSize, page.getTotal(), page.getRecords(), null);
    }

    @Override
    public List<RestaurantAmenityEntity> getByName(String name) {
        return lambdaQuery()
                .like(RestaurantAmenityEntity::getRestaurantName, name)
                .list();
    }

    @Override
    public void removeBatch(List<Long> ids) {
        removeByIds(ids);
    }

    @Override
    public String initTestData(int count) {
        String[] restaurants = {"海底捞", "西贝莜面村", "外婆家", "鼎泰丰", "大董烤鸭",
                "太二酸菜鱼", "费大厨辣椒炒肉", "眉州东坡", "云海肴", "绿茶餐厅"};
        String[] cuisines = {"川菜", "粤菜", "湘菜", "鲁菜", "淮扬菜", "日料", "西餐", "火锅", "东北菜", "西北菜"};
        String[] cities = {"北京东城区", "上海浦东新区", "广州天河区", "深圳南山区",
                "成都锦江区", "杭州西湖区", "南京鼓楼区", "武汉江汉区", "西安雁塔区", "重庆渝中区", "四川德阳"};
        String[] amenityList = {"WiFi", "包间", "停车位", "宝宝椅", "无烟区", "露天座位",
                "舞台表演", "自助取餐", "包席", "外卖配送"};

        List<RestaurantAmenityEntity> list = new ArrayList<>();
        Random random = new Random();
        long start = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            RestaurantAmenityEntity entity = new RestaurantAmenityEntity();
            int cityIndex = random.nextInt(cities.length);
            String restaurant = restaurants[random.nextInt(restaurants.length)];
            String city = cities[cityIndex];
            String cuisine = cuisines[random.nextInt(cuisines.length)];

            entity.setRestaurantName("压测-" + restaurant + "-" + (i + 1));
            entity.setAddress(city + (random.nextBoolean() ? "美食街" : "滨河小区") + (random.nextInt(999) + 1) + "号");
            entity.setPhone("138" + String.format("%08d", random.nextInt(100000000)));
            entity.setRating(random.nextInt(5) + 1);
            entity.setCuisineType(cuisine);
            entity.setDescription("这是" + restaurant + "在" + city + "的分店，主打" + cuisine + "，欢迎光临。");
            entity.setStatus(random.nextBoolean() ? 1 : 0);

            // 按城市生成经纬度（模拟真实位置）
            double[][] cityCoords = {
                    {39.93, 116.46},  // 北京东城区
                    {31.24, 121.54},  // 上海浦东新区
                    {23.13, 113.26},  // 广州天河区
                    {22.54, 113.94},  // 深圳南山区
                    {30.67, 104.09},  // 成都锦江区
                    {30.27, 120.16},  // 杭州西湖区
                    {32.06, 118.80},  // 南京鼓楼区
                    {30.58, 114.27},  // 武汉江汉区
                    {34.26, 108.95},  // 西安雁塔区
                    {29.56, 106.50},  // 重庆渝中区
                    {31.13, 104.40},  // 四川德阳
            };
            entity.setLatitude(cityCoords[cityIndex][0] + (random.nextDouble() - 0.5) * 0.06);
            entity.setLongitude(cityCoords[cityIndex][1] + (random.nextDouble() - 0.5) * 0.06);

            // 随机选 1~5 个特色标签
            int amenityCount = random.nextInt(5) + 1;
            List<String> selected = new ArrayList<>();
            for (int j = 0; j < amenityCount; j++) {
                String a = amenityList[random.nextInt(amenityList.length)];
                if (!selected.contains(a)) selected.add(a);
            }
            entity.setAmenities(String.join(",", selected));
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());

            list.add(entity);

            // 每 1000 条批量插入一次
            if (list.size() >= 1000) {
                saveBatch(list);
                list.clear();
            }
        }
        if (!list.isEmpty()) {
            saveBatch(list);
        }

        long cost = System.currentTimeMillis() - start;
        String msg = "✅ 已生成 " + count + " 条饭店测试数据，耗时 " + cost + " ms";
        log.info(msg);
        return msg;
    }

    @Override
    public void clearTestData() {
        boolean removed = remove(new LambdaQueryWrapper<RestaurantAmenityEntity>()
                .likeRight(RestaurantAmenityEntity::getRestaurantName, "压测"));
        log.info("已清除测试数据，影响行数：{}", removed);
    }
}
