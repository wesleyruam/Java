package com.nexusbank.service.impl;

import com.nexusbank.dto.response.AccountResponse;
import com.nexusbank.dto.response.BalanceResponse;
import com.nexusbank.entity.Account;
import com.nexusbank.exception.BusinessException;
import com.nexusbank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public AccountResponse getAccount(UUID userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> BusinessException.notFound("Conta não encontrada."));
        return toResponse(account);
    }

    @Transactional(readOnly = true)
    public BalanceResponse getBalance(UUID userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> BusinessException.notFound("Conta não encontrada."));
        return BalanceResponse.builder()
                .balance(account.getBalance())
                .accountNumber(account.getAccountNumber())
                .queriedAt(Instant.now())
                .build();
    }

    private AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .agency(account.getAgency())
                .balance(account.getBalance())
                .type(account.getType())
                .build();
    }
}
