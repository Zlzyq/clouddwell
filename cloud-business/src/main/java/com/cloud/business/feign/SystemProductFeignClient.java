package com.cloud.business.feign;

import com.cloud.business.feign.dto.ProductRemoteVO;
import com.cloud.common.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "cloud-system",
        contextId = "systemProductFeignClient",
        path = "/api/system/products",
        fallback = SystemProductFeignFallback.class
)
public interface SystemProductFeignClient {

    @GetMapping("/price")
    Result<ProductRemoteVO> getPrice(@RequestParam("code") String code);
}
