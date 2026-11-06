package com.cloud.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.cloud.business.dto.HotelAmenityDTO;
import com.cloud.business.entity.HotelAmenityEntity;
import com.cloud.business.repository.HotelAmenityRepository;
import com.cloud.business.service.HotelAmenityService;
import com.cloud.common.domain.PageResult;
import com.cloud.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelAmenityImpl implements HotelAmenityService {

    private final HotelAmenityRepository hotelAmenityRepository;

    //唯一索引查询
    @Override
    public HotelAmenityEntity getById(String id) {
        Optional<HotelAmenityEntity> entity = hotelAmenityRepository.findById(id);
        if (entity.isEmpty()) {
            throw new GlobalException("内容不存在");
        }
        return entity.get();
    }

    //单字段索引查询
    @Override
    public List<HotelAmenityEntity> getByName(String hotelName) {
        return hotelAmenityRepository.findByHotelNameStartingWith(hotelName);
    }

    //不加索引
    @Override
    public List<HotelAmenityEntity> getByAddress(String address) {
        return hotelAmenityRepository.findByAddressStartingWith(address);
    }

    @Override
    public PageResult<HotelAmenityEntity> listAll(int pageNo, int pageSize) {
        Page<HotelAmenityEntity> page = hotelAmenityRepository.findAll(PageRequest.of(pageNo - 1, pageSize));
        return new PageResult<>(pageNo, pageSize, page.getTotalElements(), page.getContent(), null);
    }

    @Override
    public HotelAmenityEntity create(HotelAmenityDTO dto) {
        HotelAmenityEntity hotelAmenityEntity = BeanUtil.copyProperties(dto, HotelAmenityEntity.class);
        return hotelAmenityRepository.save(hotelAmenityEntity);
    }

    @Override
    public HotelAmenityEntity update(String id, HotelAmenityDTO dto) {
        Optional<HotelAmenityEntity> entity = hotelAmenityRepository.findById(id);
        if (entity.isEmpty()) {
            throw new GlobalException("内容不存在");
        }
        HotelAmenityEntity hotelAmenityEntity = entity.get();
        BeanUtil.copyProperties(dto, hotelAmenityEntity);
        return hotelAmenityRepository.save(hotelAmenityEntity);

    }

    @Override
    public void remove(String id) {
        Optional<HotelAmenityEntity> entity = hotelAmenityRepository.findById(id);
        if (entity.isEmpty()) {
            throw new GlobalException("内容不存在");
        }
        hotelAmenityRepository.delete(entity.get());

    }

    @Override
    public void removeBatch(List<String> ids) {
        hotelAmenityRepository.deleteAllById(ids);
    }

    @Override
    public String initTestData(int count) {
        String[] hotels = {"希尔顿", "万豪", "洲际", "凯悦", "香格里拉", "喜来登", "威斯汀",
                "皇冠假日", "丽思卡尔顿", "四季", "万达文华", "锦江之星", "如家", "汉庭",
                "七天", "全季", "亚朵", "桔子水晶", "维也纳", "海友"};
        String[] cities = {"北京东城区", "上海浦东新区", "广州天河区", "深圳南山区",
                "成都锦江区", "杭州西湖区", "南京鼓楼区", "武汉江汉区", "西安雁塔区", "重庆渝中区"};
        String[] amenitiesList = {"WiFi", "游泳池", "健身房", "停车场", "早餐", "SPA",
                "餐厅", "商务中心", "洗衣服务", "接机服务"};

        List<HotelAmenityEntity> list = new java.util.ArrayList<>();
        java.util.Random random = new java.util.Random();
        long start = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            HotelAmenityEntity entity = new HotelAmenityEntity();
            String hotel = hotels[random.nextInt(hotels.length)];
            String city = cities[random.nextInt(cities.length)];
            entity.setHotelName("压测-" + hotel + "-" + (i + 1));
            entity.setAddress(city + "测试路" + (random.nextInt(999) + 1) + "号");
            entity.setPhone("138" + String.format("%08d", random.nextInt(100000000)));
            entity.setStarRating(random.nextInt(5) + 1);
            entity.setDescription("这是" + hotel + "在" + city + "的分店，提供优质服务。");
            entity.setStatus(random.nextBoolean() ? 1 : 0);

            // 随机选1~5个待遇
            int amenityCount = random.nextInt(5) + 1;
            java.util.List<String> amens = new java.util.ArrayList<>();
            for (int j = 0; j < amenityCount; j++) {
                String a = amenitiesList[random.nextInt(amenitiesList.length)];
                if (!amens.contains(a)) amens.add(a);
            }
            entity.setAmenities(amens);

            list.add(entity);

            // 每1000条批量插入一次
            if (list.size() >= 1000) {
                hotelAmenityRepository.saveAll(list);
                list.clear();
            }
        }
        // 插入剩余不足1000条的
        if (!list.isEmpty()) {
            hotelAmenityRepository.saveAll(list);
        }

        long cost = System.currentTimeMillis() - start;
        String msg = "✅ 已生成 " + count + " 条测试数据，耗时 " + cost + " ms";
        log.info(msg);
        return msg;
    }

    @Override
    public PageResult<HotelAmenityEntity> search(String keyword, int pageNo, int pageSize) {
        if (keyword == null || keyword.isBlank()) {
            return listAll(pageNo, pageSize);
        }
        Page<HotelAmenityEntity> page = hotelAmenityRepository.search(keyword, PageRequest.of(pageNo - 1, pageSize));
        return new PageResult<>(pageNo, pageSize, page.getTotalElements(), page.getContent(), null);
    }

    @Override
    public void clearTestData() {
        List<HotelAmenityEntity> testData = hotelAmenityRepository.findByHotelNameStartingWith("压测");
        if (testData.isEmpty()) {
            log.info("没有找到测试数据");
            return;
        }
        hotelAmenityRepository.deleteAll(testData);
        log.info("已清除 {} 条测试数据", testData.size());
    }
}
