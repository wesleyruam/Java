package com.nexusbank.dto.response;

import com.nexusbank.enums.TransactionStatus;
import com.nexusbank.enums.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class TransactionResponse {
    private UUID id;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private String description;
    private String referenceId;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private Instant createdAt;
}
