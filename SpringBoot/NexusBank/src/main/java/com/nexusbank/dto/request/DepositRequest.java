
package com.nexusbank.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DepositRequest {

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor do depósito deve ser maior que zero")
    @Digits(integer = 13, fraction = 2, message = "Valor inválido")
    private BigDecimal amount;

    @Size(max = 500)
    private String description;
}

