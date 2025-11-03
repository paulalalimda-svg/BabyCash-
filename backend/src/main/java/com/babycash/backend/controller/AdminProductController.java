package com.babycash.backend.controller;

import com.babycash.backend.dto.request.ProductRequest;
import com.babycash.backend.dto.response.ProductResponse;
import com.babycash.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de productos para operaciones de administración
 * Requiere rol ADMIN
 */
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "🔐 Admin Products", description = "Endpoints de administración de productos. Requiere rol ADMIN.")
public class AdminProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Crear producto", description = "Crea un nuevo producto. Solo administradores.")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente. Solo administradores.")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto. Solo administradores.")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/toggle-featured")
    @Operation(summary = "Alternar producto destacado", description = "Marca/desmarca un producto como destacado.")
    public ResponseEntity<ProductResponse> toggleFeatured(@PathVariable Long id) {
        ProductResponse response = productService.toggleFeatured(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/toggle-enabled")
    @Operation(summary = "Activar/Desactivar producto", description = "Activa o desactiva un producto.")
    public ResponseEntity<ProductResponse> toggleEnabled(@PathVariable Long id) {
        ProductResponse response = productService.toggleEnabled(id);
        return ResponseEntity.ok(response);
    }
}
