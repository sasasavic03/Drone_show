package org.droneshow.user_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Validate Access Token
     */
    public boolean validateAccessToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get Claims from Access Token
     */
    public Claims getClaimsFromAccessToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Get User ID from Access Token
     */
    public Long getUserIdFromAccessToken(String token) {
        Claims claims = getClaimsFromAccessToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Get Role from Access Token
     */
    public String getRoleFromAccessToken(String token) {
        Claims claims = getClaimsFromAccessToken(token);
        return claims.get("role", String.class);
    }

    /**
     * Get Email from Access Token
     */
    public String getEmailFromAccessToken(String token) {
        Claims claims = getClaimsFromAccessToken(token);
        return claims.getSubject();
    }
}

