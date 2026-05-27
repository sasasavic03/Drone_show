package org.droneshow.auth_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.refresh.secret}")
    private String jwtRefreshSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh.expiration}")
    private long jwtRefreshExpiration;

    /**
     * Generate Access Token
     */
    public String generateAccessToken(Long userId, String email, String role) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Generate Refresh Token
     */
    public String generateRefreshToken(Long userId) {
        SecretKey key = Keys.hmacShaKeyFor(jwtRefreshSecret.getBytes());

        return Jwts.builder()
                .subject(userId.toString())
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtRefreshExpiration))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

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
     * Validate Refresh Token
     */
    public boolean validateRefreshToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtRefreshSecret.getBytes());
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
     * Get Claims from Refresh Token
     */
    public Claims getClaimsFromRefreshToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtRefreshSecret.getBytes());
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
     * Get User ID from Refresh Token
     */
    public Long getUserIdFromRefreshToken(String token) {
        Claims claims = getClaimsFromRefreshToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Get Email from Access Token
     */
    public String getEmailFromAccessToken(String token) {
        Claims claims = getClaimsFromAccessToken(token);
        return claims.getSubject();
    }

    /**
     * Get Role from Access Token
     */
    public String getRoleFromAccessToken(String token) {
        Claims claims = getClaimsFromAccessToken(token);
        return claims.get("role", String.class);
    }

    /**
     * Check if Access Token is Expired
     */
    public boolean isAccessTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromAccessToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Check if Refresh Token is Expired
     */
    public boolean isRefreshTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromRefreshToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}

