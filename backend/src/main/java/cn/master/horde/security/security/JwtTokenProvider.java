package cn.master.horde.security.security;

import cn.master.horde.common.constants.JwtProperties;
import cn.master.horde.service.JwtBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtProperties jwtProperties;
    private final JwtBlacklistService jwtBlacklistService;

    private SecretKey key() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
            // JWT JWA规范要求HMAC-SHA算法的密钥长度必须≥256位
            if (keyBytes.length < 32) {
                log.error("JWT secret key is too short: {} bits, minimum required: 256 bits", keyBytes.length * 8);
                throw new IllegalArgumentException(
                        "JWT secret key must be at least 256 bits as per RFC 7518, Section 3.2. Current key size: " + (keyBytes.length * 8) + " bits"
                );
            }
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            log.error("Failed to create JWT signing key: {}", e.getMessage(), e);
            throw new RuntimeException("Invalid JWT configuration", e);
        }
    }

    public String generateToken(String subject, List<String> roles) {
        return Jwts.builder()
                .subject(subject)
                .claim("roles", roles)
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date(System.currentTimeMillis() + jwtProperties.getAccessTokenValidity()))
                .signWith(key())
                .compact();
    }

    /**
     * 生成Refresh Token
     */
    public String generateRefreshToken(String subject) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenValidity()))
                .signWith(key())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 验证Refresh Token
     */
    public boolean validateRefreshToken(String token) {
        // Refresh Token不需要检查黑名单，因为主要用于刷新Access Token
        try {
            Claims claims = parseClaims(token);
            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            log.warn("JWT refresh token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateToken(String token) {
        // 检查是否在黑名单中
        if (jwtBlacklistService.isInBlacklist(token)) {
            return false;
        }

        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token).getPayload();
    }
}
