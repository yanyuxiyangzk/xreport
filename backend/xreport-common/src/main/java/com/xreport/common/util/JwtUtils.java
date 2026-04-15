package com.xreport.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.Date;

public class JwtUtils {

    private static final long EXPIRE_TIME = 86400000; // 24小时

    public static String generateToken(Long userId, String username, String secret) {
        return generateToken(userId, username, secret, EXPIRE_TIME);
    }

    public static String generateToken(Long userId, String username, String secret, long expireTime) {
        return Jwts.builder()
            .subject(String.valueOf(userId))
            .claim("username", username)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expireTime))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes()), Jwts.SIG.HS512)
            .compact();
    }

    public static Long getUserIdFromToken(String token, String secret) {
        return Long.parseLong(
            Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject()
        );
    }

    public static String getUsernameFromToken(String token, String secret) {
        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("username", String.class);
    }

    public static boolean isTokenExpired(String token, String secret) {
        try {
            return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
