package com.babycash.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de health check para monitoreo y validación del estado del servicio
 */
@RestController
@RequestMapping("/api/health")
@Tag(name = "❤️ Health", description = "Endpoint de health check para verificar el estado del servicio. No requiere autenticación. Útil para monitoreo, balanceadores de carga y pruebas de disponibilidad.")
public class HealthController {

    @GetMapping
    @Operation(
            summary = "Verificar estado del servicio",
            description = """
                    Endpoint público para verificar que el servicio esté funcionando correctamente.
                    
                    Retorna información básica del servicio incluyendo:
                    - Estado actual (UP/DOWN)
                    - Nombre del servicio
                    - Versión actual
                    - Timestamp de la consulta
                    
                    Este endpoint es útil para:
                    - Health checks de contenedores Docker/Kubernetes
                    - Monitoreo de disponibilidad (uptime monitoring)
                    - Balanceadores de carga (load balancer health checks)
                    - Validación rápida del backend desde el frontend
                    - Pruebas de conectividad
                    
                    No requiere autenticación y siempre retorna 200 OK si el servicio está activo.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Servicio funcionando correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Respuesta exitosa",
                                    value = """
                                            {
                                              "status": "UP",
                                              "service": "BabyCash Backend",
                                              "version": "1.0.0",
                                              "timestamp": "2025-10-28T10:30:00",
                                              "environment": "development"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Servicio con problemas internos (raro - generalmente no responde si hay problemas)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": "DOWN",
                                              "error": "Internal Server Error",
                                              "timestamp": "2025-10-28T10:30:00"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "BabyCash Backend");
        health.put("version", "1.0.0");
        health.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(health);
    }
}
