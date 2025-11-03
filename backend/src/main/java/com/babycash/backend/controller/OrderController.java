package com.babycash.backend.controller;

import com.babycash.backend.dto.request.CreateOrderRequest;
import com.babycash.backend.dto.response.OrderResponse;
import com.babycash.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de órdenes para gestionar pedidos del usuario autenticado
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "📦 Orders", description = "Endpoints para crear y gestionar órdenes de compra. Todos los endpoints requieren autenticación JWT. Incluye consulta de historial, tracking y cancelación de órdenes.")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(
            summary = "Crear nueva orden",
            description = """
                    Crea una nueva orden de compra a partir del carrito actual del usuario con las siguientes validaciones:
                    - Verifica que el carrito no esté vacío (máximo 50 items)
                    - Valida stock disponible para todos los productos
                    - Valida dirección de envío (10-500 caracteres)
                    - Genera número de orden único (formato: ORD-timestamp-random)
                    - Calcula totales incluyendo descuentos
                    - Vacía el carrito después de crear la orden
                    
                    La orden se crea en estado PENDING esperando pago.
                    Retorna la orden completa con todos los items y montos calculados.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dirección de envío para la orden",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateOrderRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "shippingAddress": "Calle 123 #45-67, Apartamento 501, Bogotá, Colombia"
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orden creada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id": 1,
                                              "orderNumber": "ORD-1730124000-ABC123",
                                              "userId": 1,
                                              "status": "PENDING",
                                              "shippingAddress": "Calle 123 #45-67, Apartamento 501, Bogotá",
                                              "items": [
                                                {
                                                  "id": 1,
                                                  "productId": 1,
                                                  "productName": "Body de Algodón",
                                                  "quantity": 2,
                                                  "price": 25000.00,
                                                  "subtotal": 50000.00
                                                }
                                              ],
                                              "totalAmount": 50000.00,
                                              "createdAt": "2025-10-28T10:00:00",
                                              "updatedAt": "2025-10-28T10:00:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida - Carrito vacío, dirección inválida, stock insuficiente o límite de items excedido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-10-28T10:30:00",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "El carrito está vacío",
                                              "path": "/api/orders"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping
    @Operation(
            summary = "Obtener mis órdenes",
            description = """
                    Retorna una lista paginada de todas las órdenes del usuario autenticado
                    ordenadas por fecha de creación (más recientes primero).
                    
                    Incluye información completa de cada orden: items, montos, estado, dirección de envío, etc.
                    Útil para mostrar el historial de compras del usuario.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de órdenes obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "content": [
                                                {
                                                  "id": 2,
                                                  "orderNumber": "ORD-1730124500-XYZ789",
                                                  "status": "COMPLETED",
                                                  "totalAmount": 75000.00,
                                                  "createdAt": "2025-10-28T11:00:00"
                                                },
                                                {
                                                  "id": 1,
                                                  "orderNumber": "ORD-1730124000-ABC123",
                                                  "status": "PENDING",
                                                  "totalAmount": 50000.00,
                                                  "createdAt": "2025-10-28T10:00:00"
                                                }
                                              ],
                                              "pageable": {
                                                "pageNumber": 0,
                                                "pageSize": 10
                                              },
                                              "totalElements": 2,
                                              "totalPages": 1
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamaño de página", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(orderService.getMyOrders(pageable));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener orden por ID",
            description = """
                    Obtiene los detalles completos de una orden específica por su ID.
                    Verifica que la orden pertenezca al usuario autenticado.
                    
                    Incluye todos los items de la orden, montos, estado, dirección de envío,
                    fechas de creación y actualización, y número de orden para tracking.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orden encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id": 1,
                                              "orderNumber": "ORD-1730124000-ABC123",
                                              "userId": 1,
                                              "status": "PROCESSING",
                                              "shippingAddress": "Calle 123 #45-67, Apartamento 501, Bogotá",
                                              "items": [
                                                {
                                                  "id": 1,
                                                  "productId": 1,
                                                  "productName": "Body de Algodón",
                                                  "quantity": 2,
                                                  "price": 25000.00,
                                                  "subtotal": 50000.00,
                                                  "imageUrl": "https://example.com/body.jpg"
                                                }
                                              ],
                                              "totalAmount": 50000.00,
                                              "createdAt": "2025-10-28T10:00:00",
                                              "updatedAt": "2025-10-28T10:15:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado - La orden no pertenece al usuario",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-10-28T10:30:00",
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "No tienes permiso para acceder a esta orden",
                                              "path": "/api/orders/1"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Orden no encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-10-28T10:30:00",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Orden no encontrada con id: 999",
                                              "path": "/api/orders/999"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "ID único de la orden", example = "1", required = true)
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(
            summary = "Obtener orden por número de orden",
            description = """
                    Busca una orden por su número único de tracking (formato: ORD-timestamp-random).
                    Verifica que la orden pertenezca al usuario autenticado.
                    
                    Útil para sistemas de tracking donde el usuario proporciona el número de orden
                    para consultar el estado de su pedido.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orden encontrada por número",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado - La orden no pertenece al usuario",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Orden no encontrada con ese número",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-10-28T10:30:00",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Orden no encontrada con número: ORD-INVALID",
                                              "path": "/api/orders/number/ORD-INVALID"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<OrderResponse> getOrderByNumber(
            @Parameter(description = "Número único de la orden (formato: ORD-timestamp-random)", example = "ORD-1730124000-ABC123", required = true)
            @PathVariable String orderNumber
    ) {
        return ResponseEntity.ok(orderService.getOrderByNumber(orderNumber));
    }

    @PutMapping("/{id}/cancel")
    @Operation(
            summary = "Cancelar orden",
            description = """
                    Cancela una orden existente del usuario autenticado con las siguientes validaciones:
                    - Verifica que la orden pertenezca al usuario
                    - Solo se pueden cancelar órdenes en estado PENDING o PROCESSING
                    - Órdenes COMPLETED, CANCELLED o REFUNDED no pueden cancelarse
                    - Restaura el stock de los productos de la orden
                    - Cambia el estado de la orden a CANCELLED
                    
                    Retorna la orden actualizada con el nuevo estado.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orden cancelada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id": 1,
                                              "orderNumber": "ORD-1730124000-ABC123",
                                              "status": "CANCELLED",
                                              "totalAmount": 50000.00,
                                              "updatedAt": "2025-10-28T10:45:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "No se puede cancelar - Orden en estado no cancelable",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-10-28T10:30:00",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "No se puede cancelar una orden en estado COMPLETED",
                                              "path": "/api/orders/1/cancel"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado - La orden no pertenece al usuario",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Orden no encontrada",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<OrderResponse> cancelOrder(
            @Parameter(description = "ID de la orden a cancelar", example = "1", required = true)
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    @PutMapping("/{id}/status")
    @Operation(
            summary = "Actualizar estado de orden (Admin/Testing)",
            description = "Actualiza el estado de una orden. Cuando se marca como DELIVERED, se otorgan automáticamente puntos de fidelidad."
    )
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request.getStatus()));
    }

    // Request DTO
    public static class UpdateOrderStatusRequest {
        private com.babycash.backend.model.enums.OrderStatus status;
        
        public com.babycash.backend.model.enums.OrderStatus getStatus() {
            return status;
        }
        
        public void setStatus(com.babycash.backend.model.enums.OrderStatus status) {
            this.status = status;
        }
    }
}
