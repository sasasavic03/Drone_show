package org.droneshow.user_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Validate an access token.
     */
    public boolean validateAccessToken(String token) {
        try {
            token = removeBearerPrefix(token);

            Claims claims = extractClaims(token);

            return claims.getExpiration() != null
                    && claims.getExpiration().after(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get user ID from JWT.
     *
     * Expected claim:
     * {
     *   "userId": 123
     * }
     */
    public Long getUserIdFromAccessToken(String token) {
        token = removeBearerPrefix(token);

        Claims claims = extractClaims(token);

        Object userId = claims.get("userId");

        if (userId == null) {
            throw new IllegalArgumentException("userId claim is missing from token");
        }

        if (userId instanceof Number) {
            return ((Number) userId).longValue();
        }

        return Long.parseLong(userId.toString());
    }

    /**
     * Get role from JWT.
     *
     * Expected claim:
     * {
     *   "role": "ADMIN"
     * }
     */
    public String getRoleFromAccessToken(String token) {
        token = removeBearerPrefix(token);

        Claims claims = extractClaims(token);

        String role = claims.get("role", String.class);

        if (role == null) {
            throw new IllegalArgumentException("role claim is missing from token");
        }

        return role;
    }

    /**
     * Extract username/subject from token.
     */
    public String extractUsername(String token) {
        token = removeBearerPrefix(token);
        return extractClaims(token).getSubject();
    }

    /**
     * Generate a basic token.
     *
     * Note: the authentication service should normally generate
     * the access token containing userId and role.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24)
                )
                .signWith(secretKey)
                .compact();
    }

    /**
     * Backwards-compatible validation method.
     */
    public boolean validateToken(String token, String username) {
        try {
            token = removeBearerPrefix(token);

            String extractedUsername = extractUsername(token);

            return extractedUsername.equals(username)
                    && validateAccessToken(token);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parse and verify JWT.
     */
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Accept both:
     *
     * Authorization: eyJ...
     *
     * and:
     *
     * Authorization: Bearer eyJ...
     */
    private String removeBearerPrefix(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }

        token = token.trim();

        if (token.startsWith("Bearer ")) {
            return token.substring(7).trim();
        }

        return token;
    }
}