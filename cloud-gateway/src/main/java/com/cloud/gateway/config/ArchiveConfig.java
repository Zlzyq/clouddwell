package com.cloud.gateway.config;

import com.cloud.gateway.archive.ArchiveProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ArchiveProperties.class)
public class ArchiveConfig {
}
