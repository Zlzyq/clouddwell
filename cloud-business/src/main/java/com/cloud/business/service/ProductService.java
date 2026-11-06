package com.cloud.business.service;

import com.cloud.business.dto.ProductSaveDTO;
import com.cloud.business.vo.ProductVO;

import java.util.List;

public interface ProductService {

    List<ProductVO> listAll();

    ProductVO getById(Long id);

    ProductVO create(ProductSaveDTO dto);

    ProductVO update(Long id, ProductSaveDTO dto);

    void remove(Long id);

    List<ProductVO> reset();

    //根据code查询商品信息
    ProductVO getByCode(String code);

    //扣除库存
    Boolean deductStock(String code, Long quantity);

    //回复库存
    Boolean restoreStock(String code, Long quantity);

    //lua脚本扣除库存
    Boolean deDuctStockByRedis(String code, Long quantity);

    //lua脚本恢复库存
    Boolean restoreStockByRedis(String code, Long quantity);
}
