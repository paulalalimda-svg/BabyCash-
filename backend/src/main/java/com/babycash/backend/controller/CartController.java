package com.babycash.backend.controller;

import com.babycash.backend.dto.request.AddToCartRequest;
import com.babycash.backend.dto.response.CartResponse;
import com.babycash.backend.service.CartService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador del carrito de compras para gestionar items del usuario autenticado
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "🛒 Cart", description = "Endpoints para gestionar el carrito de compras del usuario autenticado. Todos los endpoints requieren token JWT en el header Authorization.")
@SecurityRequirement(name = "Bearer Authentication")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    @Operation(
            summary = "Agregar producto al carrito",
            description = """
                    Agrega un producto al carrito del usuario autenticado con validaciones:
                    - Verifica que el producto exista y esté disponible
                    - Valida stock suficiente
                    - Valida cantidad entre 1 y 99
                    - Si el producto ya está en el carrito, suma la cantidad
                    
                    Retorna el carrito actualizado con todos los items y el total calculado.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Producto y cantidad a agregar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddToCartRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "productId": 1,
                                              "quantity": 2
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto agregado al carrito exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CartResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id": 1,
                                              "userId": 1,
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
                                              "totalItems": 2,
                                              "totalAmount": 50000.00,
                                              "createdAt": "2025-10-28T10:00:00",
                                              "updatedAt": "2025-10-28T10:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida - Cantidad fuera de rango, stock insuficiente o producto no disponible",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-10-28T10:30:00",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Stock insuficiente. Disponible: 5",
                                              "path": "/api/cart/add"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado - Token JWT faltante o inválido",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(request));
    }

    @GetMapping
    @Operation(
            summary = "Obtener carrito actual",
            description = """
                    Obtiene el carrito completo del usuario autenticado incluyendo:
                    - Lista de todos los items con detalles del producto
                    - Cantidad total de items
                    - Monto total calculado
                    - Fechas de creación y última actualización
                    
                    Si el usuario no tiene carrito, se crea uno nuevo vacío automáticamente.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Carrito obtenido exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CartResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id": 1,
                                              "userId": 1,
                                              "items": [
                                                {
                                                  "id": 1,
                                                  "productId": 1,
                                                  "productName": "Body de Algodón",
                                                  "quantity": 2,
                                                  "price": 25000.00,
                                                  "subtotal": 50000.00,
                                                  "imageUrl": "https://example.com/body.jpg"
                                                },
                                                {
                                                  "id": 2,
                                                  "productId": 5,
                                                  "productName": "Peluche Musical",
                                                  "quantity": 1,
                                                  "price": 45000.00,
                                                  "subtotal": 45000.00,
                                                  "imageUrl": "https://example.com/peluche.jpg"
                                                }
                                              ],
                                              "totalItems": 3,
                                              "totalAmount": 95000.00,
                                              "createdAt": "2025-10-28T10:00:00",
                                              "updatedAt": "2025-10-28T10:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado - Token JWT faltante o inválido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-10-28T10:30:00",
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "Token JWT inválido o expirado",
                                              "path": "/api/cart"
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
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @PutMapping("/items/{itemId}")
    @Operation(
            summary = "Actualizar cantidad de un item",
            description = """
                    Actualiza la cantidad de un item específico del carrito con validaciones:
                    - Verifica que el item pertenezca al usuario autenticado
                    - Valida cantidad entre 1 y 99
                    - Verifica stock disponible del producto
                    
                    Retorna el carrito completo actualizado con nuevos totales.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cantidad actualizada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CartResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cantidad inválida o stock insuficiente",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-10-28T10:30:00",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "La cantidad debe estar entre 1 y 99",
                                              "path": "/api/cart/items/1"
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
                    responseCode = "404",
                    description = "Item no encontrado en el carrito",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-10-28T10:30:00",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Item no encontrado en el carrito",
                                              "path": "/api/cart/items/999"
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
    public ResponseEntity<CartResponse> updateCartItem(
            @Parameter(description = "ID del item en el carrito", example = "1", required = true)
            @PathVariable Long itemId,
            
            @Parameter(description = "Nueva cantidad (1-99)", example = "3", required = true)
            @RequestParam Integer quantity
    ) {
        return ResponseEntity.ok(cartService.updateCartItem(itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(
            summary = "Eliminar item del carrito",
            description = """
                    Elimina un item específico del carrito del usuario autenticado.
                    Verifica que el item pertenezca al usuario antes de eliminarlo.
                    
                    Retorna 204 No Content si la eliminación es exitosa.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Item eliminado exitosamente",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Item no encontrado en el carrito",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-10-28T10:30:00",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Item no encontrado",
                                              "path": "/api/cart/items/999"
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
    public ResponseEntity<Void> removeFromCart(
            @Parameter(description = "ID del item a eliminar", example = "1", required = true)
            @PathVariable Long itemId
    ) {
        cartService.removeFromCart(itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    @Operation(
            summary = "Vaciar carrito completo",
            description = """
                    Elimina todos los items del carrito del usuario autenticado.
                    Útil para cuando el usuario quiere empezar de nuevo o después de completar una orden.
                    
                    Retorna 204 No Content si la operación es exitosa.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Carrito vaciado exitosamente",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado - Token JWT faltante o inválido",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}
