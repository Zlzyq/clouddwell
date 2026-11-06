package com.cloud.system.service;

import com.cloud.system.dto.ProductPriceQueryDTO;
import com.cloud.system.vo.ProductPriceVO;

public interface ProductReadService {

    ProductPriceVO getPrice(ProductPriceQueryDTO dto);
}
