package com.cloud.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

/**
 * 演示页：根路径重定向到 index.html（静态资源位于 classpath:/static/）。
 */
@Configuration
public class DemoWebConfig {

    @Bean
    public RouterFunction<ServerResponse> demoIndexRedirect() {
        return RouterFunctions.route(GET("/"), request ->
                ServerResponse.temporaryRedirect(URI.create("/login.html")).build());
    }
}
