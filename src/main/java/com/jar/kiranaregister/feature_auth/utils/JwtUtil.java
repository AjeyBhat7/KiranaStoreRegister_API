package com.jar.kiranaregister.feature_auth.utils;

import com.jar.kiranaregister.exception.JwtValidationException;
import com.jar.kiranaregister.exception.TokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final Key SECRET_KEY =
            Keys.hmacShaKeyFor("bikwanJNSDbkbkBKHu8yuerk3jb8R4KHi9JBKiy8HKhJHVb".getBytes());

    /**
     * generate token from user details and set specified claims
     *
     * @param userId
     * @param roles
     * @return
     */
    public String generateToken(String userId, List<String> roles) {
        return Jwts.builder()
                .claim("roles", roles)
                .setSubject(userId) // Use MongoDB _id as the subject
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1-hour expiry
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * validates jwt token or throw error
     *
     * @param token
     * @param userId
     * @return
     */
    public boolean validateToken(String token, String userId) {
        try {
            return extractUserId(token).equals(userId) && !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            throw new JwtValidationException("JWT token is expired.");
        } catch (UnsupportedJwtException e) {
            throw new JwtValidationException("JWT token is unsupported.");
        } catch (MalformedJwtException e) {
            throw new JwtValidationException("JWT token is malformed.");
        } catch (SignatureException e) {
            throw new JwtValidationException("Invalid JWT signature.");
        } catch (IllegalArgumentException e) {
            throw new JwtValidationException("JWT token is invalid.");
        }
    }

    /**
     * extracts roles from token
     * @param token
     * @return
     */
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    /**
     * extract user id from token
     * @param token
     * @return
     */
    public String extractUserId(String token) {
        try {
            return extractClaim(token, Claims::getSubject); // Get _id from JWT
        } catch (Exception e) {
            throw new TokenException("Failed to extract user ID from token.");
        }
    }

    /**
     * extract expiry date from token
     * @param token
     * @return
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * returns token is expired or not.
     * @param token
     * @return
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * extracts claims from token.
     * @param token
     * @param claimsResolver
     * @return
     * @param <T>
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims =
                    Jwts.parserBuilder()
                            .setSigningKey(SECRET_KEY)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            throw new TokenException("Error while extracting claim from token.");
        }
    }
}
