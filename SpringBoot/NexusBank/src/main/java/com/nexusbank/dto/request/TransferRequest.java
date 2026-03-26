package com.nexusbank.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferRequest {

    @NotBlank(message = "Número da conta de destino é obrigatório")
    private String destinationAccountNumber;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    @Digits(integer = 13, fraction = 2, message = "Valor inválido")
    private BigDecimal amount;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String description;

    @NotBlank(message = "Chave de idempotência é obrigatória")
    @Size(max = 255)
    private String idempotencyKey;
}


