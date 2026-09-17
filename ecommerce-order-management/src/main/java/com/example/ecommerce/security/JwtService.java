package com.example.ecommerce.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.example.ecommerce.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final String secret =
            "mySecretKeyForEcommerceOrderManagementApplication2026";

    private final long expiration =
            1000 * 60 * 60;

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {

        Date now = new Date();

        Date expiry =
                new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("name", user.getName())
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String token) {

        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(
            String token,
            String email) {

        String tokenEmail =
                extractEmail(token);

        return tokenEmail.equals(email)
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(
            String token) {

        Date expirationDate =
                getClaims(token).getExpiration();

        return expirationDate.before(
                new Date());
    }

    private Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}