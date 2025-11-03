package com.babycash.backend.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que agrega headers de seguridad a todas las respuestas HTTP
 * 
 * Headers implementados:
 * - Content-Security-Policy (CSP): Previene XSS
 * - X-Content-Type-Options: Previene MIME sniffing
 * - X-Frame-Options: Previene clickjacking
 * - X-XSS-Protection: Protección adicional contra XSS
 * - Strict-Transport-Security: Fuerza HTTPS
 * - Referrer-Policy: Controla información de referrer
 * - Permissions-Policy: Controla APIs del navegador
 */
@Slf4j
@Component
public class SecurityHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Content Security Policy - Previene XSS y injection attacks
        response.setHeader("Content-Security-Policy",
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
            "style-src 'self' 'unsafe-inline'; " +
            "img-src 'self' data: https:; " +
            "font-src 'self' data:; " +
            "connect-src 'self'; " +
            "frame-ancestors 'none'; " +
            "base-uri 'self'; " +
            "form-action 'self';"
        );

        // X-Content-Type-Options - Previene MIME type sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");

        // X-Frame-Options - Previene clickjacking
        response.setHeader("X-Frame-Options", "DENY");

        // X-XSS-Protection - Habilita filtro XSS del navegador
        response.setHeader("X-XSS-Protection", "1; mode=block");

        // Strict-Transport-Security (HSTS) - Fuerza HTTPS por 1 año
        // Solo en producción con HTTPS
        if (request.isSecure()) {
            response.setHeader("Strict-Transport-Security", 
                "max-age=31536000; includeSubDomains; preload");
        }

        // Referrer-Policy - Controla información de referrer enviada
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // Permissions-Policy - Controla acceso a APIs del navegador
        response.setHeader("Permissions-Policy",
            "geolocation=(), " +
            "microphone=(), " +
            "camera=(), " +
            "payment=(), " +
            "usb=(), " +
            "magnetometer=(), " +
            "gyroscope=(), " +
            "accelerometer=()"
        );

        // Cache-Control para endpoints sensibles
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/auth/") || 
            requestURI.startsWith("/api/admin/") ||
            requestURI.startsWith("/api/orders/") ||
            requestURI.startsWith("/api/payments/")) {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, private");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // No aplicar a Swagger UI y recursos estáticos
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") || 
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/actuator");
    }
}
