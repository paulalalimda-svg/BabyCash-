package com.babycash.backend.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Filtro de Rate Limiting que intercepta todas las peticiones
 * y aplica límites basados en IP y tipo de endpoint
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitConfig rateLimitConfig;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String clientIP = getClientIP(request);

        // Determinar qué bucket usar según el endpoint
        Bucket bucket = selectBucket(requestURI, clientIP);

        if (bucket != null) {
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

            if (probe.isConsumed()) {
                // Request permitido - agregar headers de rate limit
                response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
                filterChain.doFilter(request, response);
            } else {
                // Rate limit excedido
                long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
                
                log.warn("Rate limit exceeded for IP: {} on endpoint: {}", clientIP, requestURI);
                
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));

                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Too Many Requests");
                errorResponse.put("message", "Has excedido el límite de peticiones. Por favor espera " + waitForRefill + " segundos.");
                errorResponse.put("retryAfter", waitForRefill);
                errorResponse.put("timestamp", System.currentTimeMillis());

                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            }
        } else {
            // Sin rate limiting para este endpoint
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Selecciona el bucket apropiado según el tipo de endpoint
     */
    private Bucket selectBucket(String requestURI, String clientIP) {
        // Endpoints de autenticación: límite estricto
        if (requestURI.startsWith("/api/auth/login") || 
            requestURI.startsWith("/api/auth/register")) {
            return rateLimitConfig.resolveAuthBucket(clientIP);
        }

        // Endpoints de admin: límite medio
        if (requestURI.startsWith("/api/admin/")) {
            return rateLimitConfig.resolveAdminBucket(clientIP);
        }

        // API general: límite generoso
        if (requestURI.startsWith("/api/")) {
            return rateLimitConfig.resolveApiBucket(clientIP);
        }

        // No aplicar rate limiting a:
        // - Actuator endpoints
        // - Swagger UI
        // - Health checks
        return null;
    }

    /**
     * Obtiene la IP real del cliente considerando proxies y balanceadores
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }

        String xrHeader = request.getHeader("X-Real-IP");
        if (xrHeader != null && !xrHeader.isEmpty()) {
            return xrHeader;
        }

        return request.getRemoteAddr();
    }
}
