package com.cloud.common.util;

import com.cloud.common.exception.GlobalException;
import com.cloud.common.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT 签发与验签（HS256）。
 */
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String createAccessToken(String subject, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        long exp = now + jwtProperties.getAccessTokenTtlSeconds() * 1000L;
        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date(now))
                .expiration(new Date(exp))
                .signWith(signingKey())
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception ex) {
            throw new GlobalException("凭证无效或已过期");
        }
    }

    public Long parseUid(String token) {
        Claims claims = parseToken(token);
        Object uid = claims.get("uid");
        if (uid == null) {
            throw new GlobalException("凭证缺少用户标识");
        }
        if (uid instanceof Number n) {
            return n.longValue();
        }
        return Long.parseLong(uid.toString());
    }

    private SecretKey signingKey() {
        String secret = jwtProperties.getSecret();
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret.trim());
        } catch (IllegalArgumentException | DecodingException ex) {
            /* 演示 YAML 为 UTF-8 明文（含 '-' 等字符会被误判为 Base64 片段后解码失败） */
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        if (keyBytes.length < 32) {
            try {
                java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
                keyBytes = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            } catch (java.security.NoSuchAlgorithmException e) {
                throw new GlobalException("JWT 密钥不可用");
            }
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
