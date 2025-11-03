package com.babycash.backend.config.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuración de Rate Limiting usando Bucket4j
 * 
 * Implementa el patrón Token Bucket para limitar requests por IP:
 * - Login: 10 requests/minuto (protección contra brute force)
 * - General API: 100 requests/minuto (previene abuse)
 * - Admin: 50 requests/minuto (operaciones sensibles)
 */
@Configuration
public class RateLimitConfig {

    // Cache de buckets por IP (en producción usar Redis para cluster)
    private final Map<String, Bucket> authBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> apiBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> adminBuckets = new ConcurrentHashMap<>();

    /**
     * Obtiene o crea un bucket para endpoints de autenticación
     * Límite: 10 requests por minuto
     */
    public Bucket resolveAuthBucket(String key) {
        return authBuckets.computeIfAbsent(key, k -> createAuthBucket());
    }

    /**
     * Obtiene o crea un bucket para API general
     * Límite: 100 requests por minuto
     */
    public Bucket resolveApiBucket(String key) {
        return apiBuckets.computeIfAbsent(key, k -> createApiBucket());
    }

    /**
     * Obtiene o crea un bucket para operaciones admin
     * Límite: 50 requests por minuto
     */
    public Bucket resolveAdminBucket(String key) {
        return adminBuckets.computeIfAbsent(key, k -> createAdminBucket());
    }

    /**
     * Crea bucket para autenticación: 10 req/min
     * Refill: 10 tokens cada 1 minuto
     */
    private Bucket createAuthBucket() {
        Bandwidth limit = Bandwidth.classic(
            10, // capacidad
            Refill.intervally(10, Duration.ofMinutes(1)) // refill
        );
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

    /**
     * Crea bucket para API general: 100 req/min
     * Refill: 100 tokens cada 1 minuto
     */
    private Bucket createApiBucket() {
        Bandwidth limit = Bandwidth.classic(
            100,
            Refill.intervally(100, Duration.ofMinutes(1))
        );
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

    /**
     * Crea bucket para admin: 50 req/min
     * Refill: 50 tokens cada 1 minuto
     */
    private Bucket createAdminBucket() {
        Bandwidth limit = Bandwidth.classic(
            50,
            Refill.intervally(50, Duration.ofMinutes(1))
        );
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

    /**
     * Limpia buckets antiguos periódicamente para evitar memory leaks
     * En producción, usar Redis con TTL automático
     */
    public void cleanupOldBuckets() {
        if (authBuckets.size() > 10000) {
            authBuckets.clear();
        }
        if (apiBuckets.size() > 10000) {
            apiBuckets.clear();
        }
        if (adminBuckets.size() > 10000) {
            adminBuckets.clear();
        }
    }
}
