package com.cloud.business.handler;

import com.cloud.business.dto.SubmitOrderDTO;
import com.cloud.business.feign.SystemProductFeignClient;
import com.cloud.business.feign.SystemUserFeignClient;
import com.cloud.business.feign.dto.ProductRemoteVO;
import com.cloud.business.feign.dto.UserRemoteVO;
import com.cloud.business.vo.OrderIntegrationResult;
import com.cloud.common.base.BaseHandler;
import com.cloud.common.domain.Result;
import com.cloud.common.enums.ResultCode;
import com.cloud.common.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 收口用户、商品远程查询（可按需扩展熔断与重试）。
 */
@Component
@RequiredArgsConstructor
public class OrderHandler extends BaseHandler<SubmitOrderDTO, OrderIntegrationResult> {

    private final SystemUserFeignClient systemUserFeignClient;
    private final SystemProductFeignClient systemProductFeignClient;

    @Override
    public OrderIntegrationResult execute(SubmitOrderDTO request) {
        beforeRemote(request);
        OrderIntegrationResult result = new OrderIntegrationResult();

        Result<UserRemoteVO> userResp = systemUserFeignClient.getUser(request.getBuyerUserId());
        UserRemoteVO user = unwrap(userResp, "查询用户失败");
        result.setUser(user);

        Result<ProductRemoteVO> priceResp =
                systemProductFeignClient.getPrice(request.getProductCode().trim());
        ProductRemoteVO product = unwrap(priceResp, "查询商品价格失败");
        if (!product.getProductCode().equalsIgnoreCase(request.getProductCode().trim())) {
            product.setProductCode(request.getProductCode().trim());
        }
        result.setProduct(product);

        afterRemote(request, result);
        return result;
    }

    private static <T> T unwrap(Result<T> result, String bizMsg) {
        if (result == null || result.getCode() != ResultCode.SUCCESS.getCode() || result.getData() == null) {
            String msg = result != null ? result.getMessage() : bizMsg;
            throw new GlobalException(ResultCode.REMOTE_CALL_ERROR, bizMsg + "：" + msg);
        }
        return result.getData();
    }
}
