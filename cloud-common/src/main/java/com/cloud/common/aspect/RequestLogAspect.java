package com.cloud.common.aspect;

import com.cloud.common.constant.MdcKeys;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Web 请求入参 / 出参 / 耗时日志（避免打印超大对象可后续扩展）。
 */
@Aspect
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class RequestLogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLogAspect.class);

    private final ObjectMapper objectMapper;

    public RequestLogAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllers() {
    }

    @Around("controllers()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String uri = "";
        String ip = "";
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest req = attrs.getRequest();
                uri = req.getRequestURI();
                ip = Optional.ofNullable(req.getHeader("X-Forwarded-For")).orElse(req.getRemoteAddr());
            }
        } catch (Exception ignored) {
        }
        String argsJson = stringify(pjp.getArgs());
        LOGGER.info("[HTTP] uri={} ip={} userIdMDC={} args={}",
                uri, ip, Optional.ofNullable(org.slf4j.MDC.get(MdcKeys.USER_ID)).orElse("-"), argsJson);
        try {
            Object result = pjp.proceed();
            long cost = System.currentTimeMillis() - start;
            LOGGER.info("[HTTP] uri={} costMs={} resp={}", uri, cost, stringify(result));
            return result;
        } catch (Throwable ex) {
            long cost = System.currentTimeMillis() - start;
            LOGGER.error("[HTTP] uri={} costMs={} error={}", uri, cost, ex.toString(), ex);
            throw ex;
        }
    }

    private String stringify(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj.getClass().isArray() && byte[].class.isAssignableFrom(obj.getClass())) {
            return "(binary)";
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return String.valueOf(obj);
        }
    }
}
