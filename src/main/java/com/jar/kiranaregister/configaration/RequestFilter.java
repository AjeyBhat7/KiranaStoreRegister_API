package com.jar.kiranaregister.configaration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jar.kiranaregister.model.User;
import com.jar.kiranaregister.repository.UserRepository;
import com.jar.kiranaregister.service.serviceImplementation.CustomUserDetailsService;
import com.jar.kiranaregister.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class RequestFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepo;

    @Autowired
    public RequestFilter(CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil, UserRepository userRepo) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")
                && !requestTokenHeader.trim().equals("Bearer null")) {
            jwtToken = requestTokenHeader.substring(7);
            Boolean isExpired = this.jwtUtil.isTokenExpired(jwtToken);

            if (isExpired) {
                sendErrorResponse(response, "Token expired", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            try {
                username = this.jwtUtil.extractUsername(jwtToken);
                User user = userRepo.findByUserName(username);

                if (user != null) {
                    sendErrorResponse(response, "User is disabled", HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    log.info("Token not validated");
                }
            } catch (Exception e) {
                log.error("Error during authentication: ", e);
            }
        }

        filterChain.doFilter(request, response);
    }


    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Response customResponse = new Response(message, false);
        String jsonResponse = objectMapper.writeValueAsString(customResponse);
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
    }

    // Assuming this is your custom Response class
    public static class Response {
        private String message;
        private boolean success;

        public Response(String message, boolean success) {
            this.message = message;
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public boolean isSuccess() {
            return success;
        }
    }
}
