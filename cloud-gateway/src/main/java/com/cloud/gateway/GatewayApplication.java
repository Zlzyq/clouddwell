package com.cloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关启动类（WebFlux）。
 */
@SpringBootApplication(
        scanBasePackages = "com.cloud.gateway",
        excludeName = "com.cloud.common.config.CloudCommonAutoConfiguration"
)
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
