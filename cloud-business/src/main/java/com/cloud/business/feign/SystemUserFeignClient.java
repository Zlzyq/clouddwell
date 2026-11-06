package com.cloud.business.feign;

import com.cloud.business.feign.dto.UserRemoteVO;
import com.cloud.common.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "cloud-system",
        contextId = "systemUserFeignClient",
        path = "/api/system/users",
        fallback = SystemUserFeignFallback.class
)
public interface SystemUserFeignClient {

    @GetMapping("/{userId}")
    Result<UserRemoteVO> getUser(@PathVariable("userId") Long userId);
}
