package com.cloud.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 配置（网关与各服务验签密钥需保持一致）。
 */
@ConfigurationProperties(prefix = "cloud.security.jwt")
public class JwtProperties {

    /** HS256 密钥（生产请使用外部环境注入长随机串）。 */
    private String secret =
            "cloud-demo-jwt-signing-secret-min-32-bytes-required-for-hs256-xx";
    /** 签发方 */
    private String issuer = "cloud-auth";
    /** 访问令牌过期秒 */
    private long accessTokenTtlSeconds = 7200L;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public long getAccessTokenTtlSeconds() {
        return accessTokenTtlSeconds;
    }

    public void setAccessTokenTtlSeconds(long accessTokenTtlSeconds) {
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
    }
}
