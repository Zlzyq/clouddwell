package com.cloud.gateway.filter;

import com.cloud.common.constant.Constants;
import com.cloud.common.constant.MdcKeys;
import com.cloud.common.util.JwtUtil;
import com.cloud.gateway.props.GatewaySecurityProperties;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Gateway 全局 JWT 校验，解析 UID 写入 {@link com.cloud.common.constant.Constants#HEADER_LOGIN_USER_ID} 下行透传。
 */
public class GatewayJwtAuthFilter implements GlobalFilter, Ordered {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private final JwtUtil jwtUtil;
    private final GatewaySecurityProperties securityProperties;
    private int order = Ordered.HIGHEST_PRECEDENCE + 10;

    public GatewayJwtAuthFilter(JwtUtil jwtUtil, GatewaySecurityProperties securityProperties) {
        this.jwtUtil = jwtUtil;
        this.securityProperties = securityProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }
        List<String> authHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null || authHeaders.isEmpty() || !StringUtils.hasText(authHeaders.get(0))) {
            return unauthorized(exchange.getResponse(), "缺少凭证");
        }
        String raw = authHeaders.get(0).trim();
        if (!raw.regionMatches(true, 0, "Bearer ", 0, "Bearer ".length())) {
            return unauthorized(exchange.getResponse(), "凭证格式错误");
        }
        String token = raw.substring("Bearer ".length()).trim();
        try {
            Long uid = jwtUtil.parseUid(token);
            MDC.put(MdcKeys.USER_ID, String.valueOf(uid));
            var mutated = exchange.getRequest().mutate()
                    .header(Constants.HEADER_LOGIN_USER_ID, String.valueOf(uid))
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build())
                    .doFinally(sig -> MDC.remove(MdcKeys.USER_ID));
        } catch (Exception ex) {
            return unauthorized(exchange.getResponse(), ex.getMessage());
        }
    }

    private boolean isWhitelisted(String path) {
        List<String> permits = securityProperties.getPermitAll();
        if (permits == null) {
            return false;
        }
        for (String p : permits) {
            if (MATCHER.match(p, path)) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> unauthorized(ServerHttpResponse resp, String msg) {
        resp.setStatusCode(HttpStatus.UNAUTHORIZED);
        resp.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"code\":401,\"message\":\"" + msg.replace("\"", "'") + "\",\"data\":null}";
        return resp.writeWith(Mono.just(resp.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8))));
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
