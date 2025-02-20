package com.jar.kiranaregister.auth.utils;

import static com.jar.kiranaregister.auth.constants.AuthConstants.*;

import com.jar.kiranaregister.exception.TokenException;
import io.jsonwebtoken.*;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtil {

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
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1-hour expiry
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates JWT token or throws an error
     *
     * @param token JWT token
     * @param userId Expected user ID
     * @return True if valid, else false
     */
    public boolean validateToken(String token, String userId) {
        try {
            return !isTokenExpired(token) && userId.equals(extractUserId(token));
        } catch (Exception e) {
            log.error(MessageFormat.format(LOG_TOKEN_VALIDATION_ERROR, e.getMessage()), e);

            throw new TokenException("Unexpected error while validating token.");
        }
    }

    /**
     * Extracts roles from token
     *
     * @param token JWT token
     * @return List of roles
     */
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    /**
     * Extracts user ID from token
     *
     * @param token JWT token
     * @return User ID
     */
    public String extractUserId(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts expiry date from token
     *
     * @param token JWT token
     * @return Expiry date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Checks if token is expired
     *
     * @param token JWT token
     * @return True if expired, else false
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts claims from token
     *
     * @param token JWT token
     * @param claimsResolver Function to extract claim
     * @param <T> Type of claim
     * @return Extracted claim
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        try {
            Claims claims =
                    Jwts.parserBuilder()
                            .setSigningKey(SECRET_KEY)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException e) {
            log.error(MessageFormat.format(LOG_TOKEN_EXPIRED, e.getMessage()), e);
            throw new TokenException("JWT token is expired.");
        } catch (UnsupportedJwtException e) {
            log.error(MessageFormat.format(LOG_TOKEN_UNSUPPORTED, e.getMessage()), e);
            throw new TokenException("JWT token is unsupported.");
        } catch (MalformedJwtException e) {
            log.error(MessageFormat.format(LOG_TOKEN_MALFORMED, e.getMessage()), e);
            throw new TokenException("JWT token is malformed.");
        }
    }
}
