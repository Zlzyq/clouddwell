package com.cloud.gateway.config;

import com.cloud.gateway.filter.GatewayJwtAuthFilter;
import com.cloud.gateway.filter.GatewayTraceFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Gateway 过滤器链路注册。
 */
@Configuration
public class GatewayFilterConfig {

    @Bean
    public GatewayTraceFilter gatewayTraceFilter() {
        GatewayTraceFilter filter = new GatewayTraceFilter();
        filter.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filter;
    }

    @Bean
    public GatewayJwtAuthFilter gatewayJwtAuthFilter(com.cloud.common.util.JwtUtil jwtUtil,
                                                     com.cloud.gateway.props.GatewaySecurityProperties props) {
        GatewayJwtAuthFilter filter = new GatewayJwtAuthFilter(jwtUtil, props);
        filter.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return filter;
    }
}
