package com.jar.kiranaregister.auth.filters;

import com.jar.kiranaregister.exception.JwtValidationException;
import com.jar.kiranaregister.exception.TokenException;
import com.jar.kiranaregister.exception.UserNotFoundException;
import com.jar.kiranaregister.auth.service.serviceImpl.CustomUserDetailsService;
import com.jar.kiranaregister.auth.utils.JwtUtil;
import com.jar.kiranaregister.feature_users.model.entity.UserInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public AuthFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * A custom security filter that intercepts incoming requests to enforce JWT authentication
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (request.getServletPath().equals("/api/v1/login")
                || request.getServletPath().equals("/api/v1/register")
                || request.getServletPath().startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        try {
            String userId = jwtUtil.extractUserId(token);
            List<String> roles = jwtUtil.extractRoles(token);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserInfo userDetails = (UserInfo) userDetailsService.loadUserByUserId(userId);

                if (userDetails == null) {
                    throw new UserNotFoundException("User not found for ID: " + userId);
                }

                if (jwtUtil.validateToken(token, userId)) {

                    List<SimpleGrantedAuthority> authorities =
                            roles.stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtValidationException | TokenException | UserNotFoundException e) {
            handleAuthenticationError(response, e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * edit the servlet response based on error.
     *
     * @param response
     * @param message
     * @throws IOException
     */
    private void handleAuthenticationError(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
        response.getWriter().flush();
    }
}
