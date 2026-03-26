
package com.nexusbank.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentRequest {

    @NotBlank(message = "Código de barras é obrigatório")
    @Size(min = 44, max = 100, message = "Código de barras inválido")
    private String barCode;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    @Digits(integer = 13, fraction = 2, message = "Valor inválido")
    private BigDecimal amount;

    @NotBlank(message = "Chave de idempotência é obrigatória")
    @Size(max = 255)
    private String idempotencyKey;
}

