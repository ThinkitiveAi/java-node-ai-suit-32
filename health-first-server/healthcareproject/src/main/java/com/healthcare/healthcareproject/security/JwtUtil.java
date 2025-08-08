package com.healthcare.healthcareproject.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final String jwtSecret = "ReplaceWithASecureSecretKeyOfAtLeast32Characters!";
    private final long jwtExpirationMs = 86400000; // 1 day
    private final Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

    public String generateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getSubjectFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String generateTokenWithClaims(String subject, String email, String role, int expiresInSeconds) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiresInSeconds * 1000L))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
} 