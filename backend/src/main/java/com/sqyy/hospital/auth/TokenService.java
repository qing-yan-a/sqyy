package com.sqyy.hospital.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class TokenService {

    private final SecretKey secretKey;
    private final long expireHours;

    public TokenService(@Value("${app.jwt.secret}") String secret,
                        @Value("${app.jwt.expire-hours}") long expireHours) {
        this.secretKey = Keys.hmacShaKeyFor(normalizeSecret(secret));
        this.expireHours = expireHours;
    }

    public String generateToken(Long userId, String username, List<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expireHours, ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private byte[] normalizeSecret(String secret) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length >= 32) {
            return bytes;
        }
        return Decoders.BASE64.decode("Y29tbXVuaXR5LWhvc3BpdGFsLWRlbW8tc2VjcmV0LWZvci1qd3QtdG9rZW4=");
    }
}
