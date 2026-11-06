package com.cloud.gateway.filter;

import com.cloud.common.constant.Constants;
import com.cloud.common.constant.MdcKeys;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * TraceId 生成与下行透传。
 */
public class GatewayTraceFilter implements GlobalFilter, Ordered {

    private int order = Ordered.HIGHEST_PRECEDENCE;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = exchange.getRequest().getHeaders().getFirst(Constants.HEADER_TRACE_ID);
        if (!StringUtils.hasText(traceId)) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        MDC.put(MdcKeys.TRACE_ID, traceId);
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(Constants.HEADER_TRACE_ID, traceId)
                .build();
        exchange.getResponse().getHeaders().set(Constants.HEADER_TRACE_ID, traceId);
        return chain.filter(exchange.mutate().request(request).build())
                .doFinally(signalType -> MDC.remove(MdcKeys.TRACE_ID));
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
