package com.cloud.business.service;

import com.cloud.business.dto.AdminOrderSaveDTO;
import com.cloud.business.vo.AdminOrderVO;
import com.cloud.common.domain.PageResult;

import java.util.List;

public interface AdminOrderService {

    List<AdminOrderVO> listAll();

    AdminOrderVO getById(Long id);

    AdminOrderVO create(AdminOrderSaveDTO dto);

    AdminOrderVO update(Long id, AdminOrderSaveDTO dto);

    void remove(Long id);

    List<AdminOrderVO> reset();

    void batchDelete(List<Long> idList);

    PageResult<AdminOrderVO> page(Integer pageNo, Integer pageSize);

    List<AdminOrderVO> searchByName(String keyword);

    /** 从真实订单同步到管理列表（不扣库存） */
    void syncFromRealOrder(String orderNo, String buyerName, String productCode,
                           String productName, Integer quantity, String amount,
                           String status, String createTime);

    /** 同步订单状态到管理列表 */
    void syncStatus(String orderNo, String status);
}
