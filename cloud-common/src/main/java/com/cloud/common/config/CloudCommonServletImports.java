package com.cloud.common.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

/**
 * 仅在 Servlet MVC 环境（存在 WebMvcConfigurer）下导入 Web 相关配置，避免 Gateway 等 WebFlux 应用启动失败。
 */
public class CloudCommonServletImports implements ImportSelector {

    private static final String WEB_MVC_CONFIGURER =
            "org.springframework.web.servlet.config.annotation.WebMvcConfigurer";

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        if (!ClassUtils.isPresent(WEB_MVC_CONFIGURER, null)) {
            return new String[0];
        }
        return new String[]{
                "com.cloud.common.config.WebAutoConfig",
                "com.cloud.common.aspect.RequestLogAspect",
                "com.cloud.common.exception.GlobalExceptionHandler"
        };
    }
}
