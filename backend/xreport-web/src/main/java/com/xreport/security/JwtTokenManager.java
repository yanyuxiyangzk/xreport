package com.xreport.security;

import com.xreport.common.util.JwtUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT Token管理器
 * 统一管理Token的生成、验证、解析和刷新
 */
@Component
public class JwtTokenManager {

    private static final String JWT_SECRET = "xreport-jwt-secret-key-must-be-at-least-512-bits-long-for-hs512";
    private static final long EXPIRE_TIME = 86400000L; // 24小时

    /**
     * 创建Token
     */
    public String createToken(Long userId, String username) {
        return createToken(userId, username, EXPIRE_TIME);
    }

    /**
     * 创建Token（指定过期时间）
     */
    public String createToken(Long userId, String username, long expireTime) {
        return JwtUtils.generateToken(userId, username, JWT_SECRET, expireTime);
    }

    /**
     * 创建Token（包含角色信息）
     */
    public String createToken(Long userId, String username, String[] roles) {
        // 在实际场景中可以将角色信息编码到token中
        // 这里简化为只使用用户ID和用户名
        return JwtUtils.generateToken(userId, username, JWT_SECRET, EXPIRE_TIME);
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        return !JwtUtils.isTokenExpired(token, JWT_SECRET);
    }

    /**
     * 解析Token获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        return JwtUtils.getUserIdFromToken(token, JWT_SECRET);
    }

    /**
     * 解析Token获取用户名
     */
    public String getUsernameFromToken(String token) {
        return JwtUtils.getUsernameFromToken(token, JWT_SECRET);
    }

    /**
     * 解析Token获取所有Claims
     */
    public Map<String, Object> getClaims(String token) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", getUserIdFromToken(token));
        claims.put("username", getUsernameFromToken(token));
        return claims;
    }

    /**
     * 刷新Token
     * 返回一个新的Token（不改变原有Token的有效性）
     */
    public String refreshToken(String token) {
        if (!validateToken(token)) {
            throw new RuntimeException("Token无效，无法刷新");
        }
        Long userId = getUserIdFromToken(token);
        String username = getUsernameFromToken(token);
        return createToken(userId, username);
    }

    /**
     * 获取Token过期时间（毫秒）
     */
    public long getExpireTime() {
        return EXPIRE_TIME;
    }
}