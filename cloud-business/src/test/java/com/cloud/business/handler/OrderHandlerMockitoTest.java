package com.cloud.business.handler;

import com.cloud.business.dto.SubmitOrderDTO;
import com.cloud.business.feign.SystemProductFeignClient;
import com.cloud.business.feign.SystemUserFeignClient;
import com.cloud.business.feign.dto.ProductRemoteVO;
import com.cloud.business.feign.dto.UserRemoteVO;
import com.cloud.business.vo.OrderIntegrationResult;
import com.cloud.common.domain.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** JUnit + Mockito：与 Groovy Spock 双栈示例共存。 */
@ExtendWith(MockitoExtension.class)
class OrderHandlerMockitoTest {

    @Mock
    private SystemUserFeignClient userFeignClient;

    @Mock
    private SystemProductFeignClient productFeignClient;

    @InjectMocks
    private OrderHandler orderHandler;

    @Test
    void executeDelegatesToBothClients() {
        SubmitOrderDTO dto = new SubmitOrderDTO();
        dto.setBuyerUserId(5L);
        dto.setProductCode("SKU-DEMO");
        dto.setQuantity(1);

        UserRemoteVO user = new UserRemoteVO();
        user.setUserId(5L);
        when(userFeignClient.getUser(5L)).thenReturn(Result.ok(user));

        ProductRemoteVO prod = new ProductRemoteVO();
        prod.setProductCode("SKU-DEMO");
        prod.setUnitPrice(BigDecimal.TEN);
        when(productFeignClient.getPrice("SKU-DEMO")).thenReturn(Result.ok(prod));

        OrderIntegrationResult res = orderHandler.execute(dto);

        Assertions.assertEquals(BigDecimal.TEN, res.getProduct().getUnitPrice());
        verify(userFeignClient).getUser(5L);
        verify(productFeignClient).getPrice("SKU-DEMO");
    }
}
