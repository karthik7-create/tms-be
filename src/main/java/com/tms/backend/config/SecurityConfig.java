// ═══ FILE: src/main/java/com/tms/backend/config/SecurityConfig.java ═══
package com.tms.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * TEMPORARY SecurityConfig — permits all requests for development/testing.
 * Karthik will replace this with full JWT-based auth in the Auth Module.
 *
 * When Auth Module is integrated, this should enforce:
 *   - Public: /api/auth/**, GET /api/movies/**, GET /api/shows/**, GET /api/theatres, /api/payments/webhook
 *   - USER:   /api/bookings/**, /api/payments/**, GET /api/auth/profile
 *   - ADMIN:  /api/admin/**
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()  // TODO: Replace with role-based rules in Auth Module
            );

        return http.build();
    }
}
