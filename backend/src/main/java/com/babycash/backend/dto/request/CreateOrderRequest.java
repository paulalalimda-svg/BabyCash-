package com.babycash.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * DTO para solicitud de creación de orden
 */
@Data
public class CreateOrderRequest {

    @NotBlank(message = "La dirección de envío es obligatoria")
    @Size(min = 10, max = 500, message = "La dirección debe tener entre 10 y 500 caracteres")
    private String shippingAddress;

    @Size(max = 1000, message = "Las notas no pueden exceder 1000 caracteres")
    private String notes;

    @NotEmpty(message = "Debe incluir al menos un producto en la orden")
    @Size(min = 1, max = 50, message = "Una orden puede tener entre 1 y 50 productos")
    @Valid
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        
        @NotNull(message = "El ID del producto es obligatorio")
        @Min(value = 1, message = "El ID del producto debe ser mayor a 0")
        private Long productId;
        
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad mínima es 1")
        @Max(value = 99, message = "La cantidad máxima por producto es 99")
        private Integer quantity;
    }
}
