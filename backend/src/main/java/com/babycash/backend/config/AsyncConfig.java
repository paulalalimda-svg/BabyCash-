package com.babycash.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuración para habilitar procesamiento asíncrono y tareas programadas
 * 
 * - @EnableAsync: Permite métodos @Async para audit logging no bloqueante
 * - @EnableScheduling: Permite tareas programadas para limpieza de datos
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {
    // Spring Boot auto-configura un ThreadPoolTaskExecutor por defecto
    // Si necesitas personalizar el pool, puedes crear un bean AsyncConfigurer
}
