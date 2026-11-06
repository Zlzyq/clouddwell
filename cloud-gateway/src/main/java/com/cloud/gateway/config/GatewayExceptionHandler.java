package com.cloud.gateway.config;

import com.cloud.common.domain.Result;
import com.cloud.common.enums.ResultCode;
import com.cloud.common.exception.GlobalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

/**
 * 网关本地 API 异常处理（WebFlux）。
 */
@RestControllerAdvice(basePackages = "com.cloud.gateway")
public class GatewayExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayExceptionHandler.class);

    @ExceptionHandler(GlobalException.class)
    public Mono<Result<Void>> handleBusiness(GlobalException ex) {
        LOGGER.warn("gateway business error code={} msg={}", ex.getCode(), ex.getMessage());
        return Mono.just(Result.fail(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<Result<Void>> handleOthers(Throwable ex) {
        LOGGER.error("gateway unhandled error", ex);
        return Mono.just(Result.fail(ResultCode.SERVER_ERROR.getCode(), ex.getMessage()));
    }
}
