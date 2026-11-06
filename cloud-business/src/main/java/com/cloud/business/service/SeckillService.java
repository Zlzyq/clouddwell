package com.cloud.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.business.dto.SeckillSaveDTO;
import com.cloud.business.entity.SeckillEntity;
import com.cloud.business.vo.SeckillVO;

import java.util.List;

/**
 * <p>
 * 秒杀活动表 服务类
 * </p>
 *
 * @author 张德帅
 * @since 2026-07-10
 */
public interface SeckillService extends IService<SeckillEntity> {

    List<SeckillVO> listAll();

    SeckillVO create(SeckillSaveDTO dto);

    SeckillVO update(Long id, SeckillSaveDTO dto);

    SeckillVO getByIdMe(Long id);

    void remove(Long id);

    void seckillOrder(Long id, Long uid, Integer quantity);
}
