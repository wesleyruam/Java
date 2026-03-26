package com.nexusbank.dto.response;

import com.nexusbank.enums.AccountType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class AccountResponse {
    private UUID id;
    private String accountNumber;
    private String agency;
    private BigDecimal balance;
    private AccountType type;
}
