package com.babycash.backend.controller;

import com.babycash.backend.dto.response.OrderResponse;
import com.babycash.backend.model.enums.OrderStatus;
import com.babycash.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de administración de órdenes
 * Solo accesible por usuarios con rol ADMIN
 */
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "🔐 Admin Orders", description = "Endpoints de administración de órdenes. Requiere rol ADMIN.")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Obtener todas las órdenes", description = "Lista paginada de todas las órdenes del sistema. Solo administradores.")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OrderResponse> orders;
        
        if (status != null) {
            orders = orderService.getAllOrdersByStatus(status, pageable);
        } else {
            orders = orderService.getAllOrders(pageable);
        }
        
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener orden por ID", description = "Obtiene cualquier orden por ID. Solo administradores.")
    public ResponseEntity<OrderResponse> getOrderByIdAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderByIdAdmin(id));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Actualizar estado de orden", description = "Cambia el estado de una orden. Solo administradores.")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @GetMapping("/stats")
    @Operation(summary = "Obtener estadísticas de órdenes", description = "Estadísticas generales de órdenes del sistema.")
    public ResponseEntity<OrderService.OrderStats> getOrderStats() {
        return ResponseEntity.ok(orderService.getOrderStats());
    }
}
