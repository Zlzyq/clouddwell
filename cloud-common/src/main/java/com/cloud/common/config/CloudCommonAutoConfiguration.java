package com.cloud.common.config;

import com.cloud.common.properties.JwtProperties;
import com.cloud.common.util.JwtUtil;
import com.cloud.common.util.RedisUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * cloud-common 自动装配入口。
 */
@AutoConfiguration
@EnableConfigurationProperties(JwtProperties.class)
@Import({
        CloudCommonServletImports.class,
        ThreadPoolConfig.class
})
public class CloudCommonAutoConfiguration {

    @Bean
    public JwtUtil jwtUtil(JwtProperties jwtProperties) {
        return new JwtUtil(jwtProperties);
    }

    /** Redis 占位 Bean；接入真实 Redis 时可改为按需装配或移除本方法。 */
    @Bean
    public RedisUtils redisUtils() {
        return new RedisUtils();
    }
}
