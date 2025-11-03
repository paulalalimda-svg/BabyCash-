package com.babycash.backend.dto.request;

import com.babycash.backend.model.enums.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para solicitud de procesamiento de pago
 */
@Data
public class ProcessPaymentRequest {

    @NotNull(message = "El ID de la orden es obligatorio")
    @Min(value = 1, message = "El ID de la orden debe ser mayor a 0")
    private Long orderId;

    @NotNull(message = "El método de pago es obligatorio")
    private PaymentMethod paymentMethod;

    // Campos opcionales para pagos con tarjeta (solo si paymentMethod es CREDIT_CARD o DEBIT_CARD)
    @Pattern(
        regexp = "^[0-9]{16}$|^$",
        message = "El número de tarjeta debe tener 16 dígitos"
    )
    private String cardNumber;

    @Size(max = 100, message = "El nombre del titular no puede exceder 100 caracteres")
    private String cardholderName;

    @Pattern(
        regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$|^$",
        message = "La fecha de vencimiento debe estar en formato MM/YY"
    )
    private String expiryDate;

    @Pattern(
        regexp = "^[0-9]{3,4}$|^$",
        message = "El CVV debe tener 3 o 4 dígitos"
    )
    private String cvv;
}
