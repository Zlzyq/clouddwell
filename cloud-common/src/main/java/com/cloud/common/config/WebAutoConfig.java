package com.cloud.common.config;

import com.cloud.common.constant.Constants;
import com.cloud.common.constant.MdcKeys;
import com.cloud.common.context.UserContextInterceptor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.UUID;

/**
 * Web 自动装配：链路 TraceId（若网关未传入则本地生成）、用户上下文拦截器。
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebAutoConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserContextInterceptor()).addPathPatterns("/**");
    }

    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> traceIdFilter() {
        FilterRegistrationBean<OncePerRequestFilter> bean = new FilterRegistrationBean<>();
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        bean.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                String tid = request.getHeader(Constants.HEADER_TRACE_ID);
                if (!StringUtils.hasText(tid)) {
                    tid = UUID.randomUUID().toString().replace("-", "");
                }
                MDC.put(MdcKeys.TRACE_ID, tid);
                try {
                    response.setHeader(Constants.HEADER_TRACE_ID, tid);
                    filterChain.doFilter(request, response);
                } finally {
                    MDC.remove(MdcKeys.TRACE_ID);
                }
            }
        });
        return bean;
    }
}
