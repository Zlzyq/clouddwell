package com.cloud.system.service.impl;

import com.cloud.common.exception.GlobalException;
import com.cloud.system.dto.ProductPriceQueryDTO;
import com.cloud.system.service.ProductReadService;
import com.cloud.system.vo.ProductPriceVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 演示商品价格表。
 */
@Service
public class ProductReadServiceImpl implements ProductReadService {

    private static final Map<String, BigDecimal> DEMO_PRICE = new HashMap<>();

    static {
        DEMO_PRICE.put("SKU-PHONE", new BigDecimal("3999.00"));
        DEMO_PRICE.put("SKU-BOOK", new BigDecimal("59.90"));
        DEMO_PRICE.put("SKU-DEMO", new BigDecimal("9.99"));
        DEMO_PRICE.put("SKU-CLOUD", new BigDecimal("299.00"));
    }

    @Override
    public ProductPriceVO getPrice(ProductPriceQueryDTO dto) {
        if (dto == null || dto.getProductCode() == null) {
            throw new GlobalException("商品编码不能为空");
        }
        BigDecimal price = DEMO_PRICE.get(dto.getProductCode().trim());
        if (price == null) {
            throw new GlobalException("商品不存在或未定价");
        }
        ProductPriceVO vo = new ProductPriceVO();
        vo.setProductCode(dto.getProductCode().trim());
        vo.setUnitPrice(price);
        return vo;
    }


}
