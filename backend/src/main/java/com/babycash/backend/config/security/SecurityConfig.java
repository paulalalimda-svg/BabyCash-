package com.babycash.backend.config.security;

import com.babycash.backend.security.CustomUserDetailsService;
import com.babycash.backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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

/**
 * Security configuration with advanced protection:
 * - JWT Authentication
 * - Rate Limiting
 * - Security Headers (CSP, HSTS, X-Frame-Options, etc.)
 * - Role-based access control
 * - Stateless session management
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;
    private final RateLimitFilter rateLimitFilter;
    private final SecurityHeadersFilter securityHeadersFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configure(http)) // HABILITAR CORS
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/api/products/**").permitAll()
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                
                // Blog public endpoints (GET operations for reading)
                .requestMatchers(
                    org.springframework.http.HttpMethod.GET,
                    "/api/blog",
                    "/api/blog/*",
                    "/api/blog/slug/**",
                    "/api/blog/featured",
                    "/api/blog/search",
                    "/api/blog/tag/**",
                    "/api/blog/most-viewed"
                ).permitAll()
                
                // Testimonials public endpoints
                .requestMatchers(
                    org.springframework.http.HttpMethod.GET,
                    "/api/testimonials",
                    "/api/testimonials/*",
                    "/api/testimonials/featured"
                ).permitAll()
                .requestMatchers(
                    org.springframework.http.HttpMethod.POST,
                    "/api/testimonials"
                ).permitAll() // Allow public testimonial submission
                
                // Contact Info public endpoint
                .requestMatchers(
                    org.springframework.http.HttpMethod.GET,
                    "/api/contact-info"
                ).permitAll()
                
                // Contact Message public endpoint
                .requestMatchers(
                    org.springframework.http.HttpMethod.POST,
                    "/api/contact/send"
                ).permitAll()
                
                // Admin endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                
                // Blog write operations require authentication
                .requestMatchers("/api/blog/**").authenticated()
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            // Order matters: Rate Limit -> Security Headers -> JWT
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(securityHeadersFilter, RateLimitFilter.class)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Strength 12 para mejor seguridad
    }
}

