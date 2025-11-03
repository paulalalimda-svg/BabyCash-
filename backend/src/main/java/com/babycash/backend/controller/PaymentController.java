package com.babycash.backend.controller;

import com.babycash.backend.dto.request.ProcessPaymentRequest;
import com.babycash.backend.dto.response.PaymentResponse;
import com.babycash.backend.service.PaymentService;
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
 * Controlador de pagos para procesar transacciones de órdenes
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "💳 Payments", description = "Endpoints para procesar pagos de órdenes. Todos los endpoints requieren autenticación JWT. Soporta múltiples métodos de pago: tarjeta de crédito/débito, PayPal, Stripe y MercadoPago.")
@SecurityRequirement(name = "Bearer Authentication")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    @Operation(
            summary = "Procesar pago de orden",
            description = """
                    Procesa el pago de una orden existente con validaciones completas:
                    - Verifica que la orden exista y pertenezca al usuario autenticado
                    - Valida que la orden esté en estado PENDING (sin pagar)
                    - Valida datos de pago según el método seleccionado:
                      * CREDIT_CARD/DEBIT_CARD: número de tarjeta (16 dígitos), CVV (3-4 dígitos), expiración (MM/YY)
                      * PAYPAL/STRIPE/MERCADOPAGO: token de pago válido
                    - Simula procesamiento de pago (en producción se integraría con gateway real)
                    - Actualiza estado de orden a PROCESSING si el pago es exitoso
                    - Genera transaction ID único
                    
                    Métodos de pago disponibles: CREDIT_CARD, DEBIT_CARD, PAYPAL, STRIPE, MERCADOPAGO
                    
                    NOTA: Esta es una implementación simulada. En producción se integraría con
                    gateways de pago reales como Stripe, PayPal o MercadoPago.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de pago de la orden",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProcessPaymentRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Tarjeta de crédito",
                                            value = """
                                                    {
                                                      "orderId": 1,
                                                      "paymentMethod": "CREDIT_CARD",
                                                      "cardNumber": "4111111111111111",
                                                      "cardHolderName": "Juan Pérez",
                                                      "expirationDate": "12/25",
                                                      "cvv": "123"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "PayPal",
                                            value = """
                                                    {
                                                      "orderId": 1,
                                                      "paymentMethod": "PAYPAL",
                                                      "paymentToken": "PAYPAL-TOKEN-ABC123XYZ"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "MercadoPago",
                                            value = """
                                                    {
                                                      "orderId": 1,
                                                      "paymentMethod": "MERCADOPAGO",
                                                      "paymentToken": "MP-TOKEN-789DEF456"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pago procesado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id": 1,
                                              "orderId": 1,
                                              "orderNumber": "ORD-1730124000-ABC123",
                                              "method": "CREDIT_CARD",
                                              "status": "COMPLETED",
                                              "transactionId": "TXN-1730124500-DEF456",
                                              "metadata": {
                                                "cardLast4": "1111",
                                                "cardBrand": "VISA"
                                              },
                                              "createdAt": "2025-10-28T10:15:00",
                                              "updatedAt": "2025-10-28T10:15:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida - Datos de pago incompletos, orden ya pagada, método de pago inválido o validación de tarjeta fallida",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Orden ya pagada",
                                            value = """
                                                    {
                                                      "timestamp": "2025-10-28T10:30:00",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "La orden ya ha sido pagada",
                                                      "path": "/api/payments/process"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Tarjeta inválida",
                                            value = """
                                                    {
                                                      "timestamp": "2025-10-28T10:30:00",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "Número de tarjeta debe tener exactamente 16 dígitos",
                                                      "path": "/api/payments/process"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado - Token JWT faltante o inválido",
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
                                              "message": "No tienes permiso para pagar esta orden",
                                              "path": "/api/payments/process"
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
                                              "path": "/api/payments/process"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor o error en gateway de pago",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-10-28T10:30:00",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "Error al procesar el pago. Intente nuevamente.",
                                              "path": "/api/payments/process"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    @GetMapping("/order/{orderId}")
    @Operation(
            summary = "Obtener pago por ID de orden",
            description = """
                    Obtiene la información completa del pago asociado a una orden específica.
                    Verifica que la orden pertenezca al usuario autenticado.
                    
                    Incluye:
                    - Método de pago utilizado
                    - Estado del pago (PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED)
                    - Transaction ID para tracking
                    - Metadata adicional (últimos 4 dígitos de tarjeta, marca, etc.)
                    - Fechas de creación y actualización
                    
                    Útil para mostrar comprobante de pago o verificar estado de transacción.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Información de pago obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id": 1,
                                              "orderId": 1,
                                              "orderNumber": "ORD-1730124000-ABC123",
                                              "method": "CREDIT_CARD",
                                              "status": "COMPLETED",
                                              "transactionId": "TXN-1730124500-DEF456",
                                              "metadata": {
                                                "cardLast4": "1111",
                                                "cardBrand": "VISA",
                                                "cardHolderName": "Juan Pérez"
                                              },
                                              "createdAt": "2025-10-28T10:15:00",
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
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pago no encontrado para la orden especificada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-10-28T10:30:00",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Pago no encontrado para la orden con id: 999",
                                              "path": "/api/payments/order/999"
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
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @Parameter(description = "ID de la orden para consultar su pago", example = "1", required = true)
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }
}
