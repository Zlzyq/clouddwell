package com.cloud.business.feign;

import com.cloud.business.feign.dto.ProductRemoteVO;
import com.cloud.common.domain.Result;
import com.cloud.common.enums.ResultCode;
import org.springframework.stereotype.Component;

@Component
public class SystemProductFeignFallback implements SystemProductFeignClient {

    @Override
    public Result<ProductRemoteVO> getPrice(String code) {
        return Result.fail(ResultCode.REMOTE_CALL_ERROR.getCode(), "商品服务降级");
    }
}
