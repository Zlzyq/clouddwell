package com.cloud.gateway.config;

import com.cloud.common.properties.JwtProperties;
import com.cloud.common.util.JwtUtil;
import com.cloud.gateway.props.GatewaySecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({GatewaySecurityProperties.class, JwtProperties.class})
public class GatewayBeansConfig {

    /** 网关为 WebFlux，不加载 cloud-common 的 Servlet 自动配置，仅注册 JWT 工具。 */
    @Bean
    public JwtUtil jwtUtil(JwtProperties jwtProperties) {
        return new JwtUtil(jwtProperties);
    }
}
