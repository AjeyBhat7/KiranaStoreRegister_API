package com.jar.kiranaregister.ratelimiting.filter;

import com.jar.kiranaregister.exception.RateLimitExceededException;
import com.jar.kiranaregister.auth.utils.JwtUtil;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ConcurrentMap<String, Bucket> userBuckets = new ConcurrentHashMap<>();

    @Autowired
    public RateLimitingFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * A custom security filter that intercepts incoming requests to enforce JWT authentication and
     * rate limiting using the Bucket4j library.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {

            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authorizationHeader.substring(7);
            String userId = jwtUtil.extractUserId(token);

            if (userId != null) {
                Bucket bucket = userBuckets.computeIfAbsent(userId, this::createNewBucket);

                if (bucket.tryConsume(1)) {
                    filterChain.doFilter(request, response);
                } else {
                    throw new RateLimitExceededException("Rate limit exceeded");
                }
            } else {
                filterChain.doFilter(request, response);
            }
        } catch (RateLimitExceededException e) {
            handleRateLimitExceeded(response);
        }
    }

    /**
     * Handles the case when a user exceeds the allowed request limit. Sends a 429 Too Many Requests
     * HTTP response.
     */
    private void handleRateLimitExceeded(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Request limit exceeded\"}");
    }

    /** Creates a new rate-limiting bucket for a user. */
    private Bucket createNewBucket(String userId) {
        return Bucket4j.builder()
                .addLimit(
                        Bandwidth.classic(
                                100,
                                Refill.intervally(
                                        10, Duration.ofSeconds(1))))
                .build();
    }
}
