package com.jar.kiranaregister.configaration;

import com.jar.kiranaregister.feature_auth.filters.AuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

        /**
         * Bean for password encoding using BCrypt hashing algorithm.
         *  This ensures passwords are securely hashed before storage.
         */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * Bean for AuthenticationManager, which handles authentication logic.
     * @param authConfig Authentication configuration provided by Spring Security.
     * @return An instance of AuthenticationManager
     **/
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }


    /**
     * Configures the security filter chain to handle request authorization, session management,
     * and authentication filtering.
     *
     * @param http       The HttpSecurity object for configuring security settings.
     * @param authFilter Custom authentication filter.
     * @return Configured SecurityFilterChain instance.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthFilter authFilter)
            throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/login", "/register", "/actuator/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
