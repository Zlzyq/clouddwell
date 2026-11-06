package com.cloud.business.config;

import com.cloud.common.constant.Constants;
import com.cloud.common.constant.MdcKeys;
import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignTracingConfig {

    @Bean
    public RequestInterceptor cloudTracePropagatingInterceptor() {
        return requestTemplate -> {
            String traceId = MDC.get(MdcKeys.TRACE_ID);
            if (traceId != null) {
                requestTemplate.header(Constants.HEADER_TRACE_ID, traceId);
            }
            String uid = MDC.get(MdcKeys.USER_ID);
            if (uid != null) {
                requestTemplate.header(Constants.HEADER_LOGIN_USER_ID, uid);
            }
        };
    }
}
