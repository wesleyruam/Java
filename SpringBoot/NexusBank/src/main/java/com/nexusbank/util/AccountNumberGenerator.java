package com.nexusbank.util;

import com.nexusbank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final AccountRepository accountRepository;

    /**
     * Generates a unique 8-digit account number.
     * Retries until a non-duplicate is found (collision is extremely rare).
     */
    public String generate() {
        String accountNumber;
        int attempts = 0;
        do {
            if (++attempts > 10) {
                throw new IllegalStateException("Não foi possível gerar um número de conta único após 10 tentativas.");
            }
            accountNumber = String.format("%08d", SECURE_RANDOM.nextInt(100_000_000));
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
