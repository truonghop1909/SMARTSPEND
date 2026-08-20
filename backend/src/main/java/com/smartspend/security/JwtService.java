package com.smartspend.security;

import com.smartspend.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = createSigningKey(jwtProperties.secret());
    }

    public String generateAccessToken(UserPrincipal principal) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(
                jwtProperties.accessTokenExpiration()
        );

        String role = principal.getAuthorities()
                .stream()
                .findFirst()
                .map(authority -> removeRolePrefix(authority.getAuthority()))
                .orElse("USER");

        return Jwts.builder()
                .subject(principal.getId().toString())
                .claim("email", principal.getUsername())
                .claim("role", role)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();
    }

    public Long extractUserId(String token) {
        String subject = extractAllClaims(token).getSubject();

        if (subject == null || subject.isBlank()) {
            throw new JwtException("JWT subject is missing");
        }

        try {
            return Long.valueOf(subject);
        } catch (NumberFormatException exception) {
            throw new JwtException("JWT subject is invalid", exception);
        }
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).get(
                "email",
                String.class
        );
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get(
                "role",
                String.class
        );
    }

    public Instant extractExpiration(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .toInstant();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();

            return expiration != null
                    && expiration.after(new Date());
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public long getAccessTokenExpirationSeconds() {
        return jwtProperties
                .accessTokenExpiration()
                .toSeconds();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey createSigningKey(String base64Secret) {
        try {
            byte[] secretBytes = Decoders.BASE64.decode(base64Secret);

            if (secretBytes.length < 32) {
                throw new IllegalArgumentException(
                        "JWT secret must contain at least 32 bytes"
                );
            }

            return Keys.hmacShaKeyFor(secretBytes);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(
                    "JWT_SECRET must be a valid Base64 string "
                            + "containing at least 32 bytes",
                    exception
            );
        }
    }

    private String removeRolePrefix(String authority) {
        if (authority != null && authority.startsWith("ROLE_")) {
            return authority.substring("ROLE_".length());
        }

        return authority;
    }
}