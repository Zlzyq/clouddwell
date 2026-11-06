package com.cloud.business.feign;

import com.cloud.business.feign.dto.UserRemoteVO;
import com.cloud.common.domain.Result;
import com.cloud.common.enums.ResultCode;
import org.springframework.stereotype.Component;

@Component
public class SystemUserFeignFallback implements SystemUserFeignClient {

    @Override
    public Result<UserRemoteVO> getUser(Long userId) {
        return Result.fail(ResultCode.REMOTE_CALL_ERROR.getCode(), "用户服务降级");
    }
}
