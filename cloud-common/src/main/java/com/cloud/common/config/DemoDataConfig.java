package com.cloud.common.config;

import com.cloud.common.properties.DemoDataProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DemoDataProperties.class)
public class DemoDataConfig {
}
