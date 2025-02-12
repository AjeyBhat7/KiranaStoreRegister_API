package com.jar.kiranaregister.filters;

import com.jar.kiranaregister.utils.JwtUtil;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class RateLimitingFilter implements Filter {

    private final JwtUtil jwtUtil;
    private final ConcurrentMap<String, Bucket> userBuckets = new ConcurrentHashMap<>();


    @Autowired
    public RateLimitingFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authorizationHeader = httpRequest.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);
        String userId = jwtUtil.extractUserId(token);

        if (userId != null) {
            Bucket bucket = userBuckets.computeIfAbsent(userId, this::createNewBucket);

            if (bucket.tryConsume(1)) {
                chain.doFilter(request, response);
            } else {
                ((HttpServletResponse) response).setStatus(429); // Too Many Requests
                response.getWriter().write("Rate limit exceeded. Try again later.");
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private Bucket createNewBucket(String userId) {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)))) // 10 requests per minute
                .build();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}
