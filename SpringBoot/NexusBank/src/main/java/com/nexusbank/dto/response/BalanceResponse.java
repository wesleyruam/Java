package com.nexusbank.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
public class BalanceResponse {
    private BigDecimal balance;
    private String accountNumber;
    private Instant queriedAt;
}
