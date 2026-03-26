package com.nexusbank.config;

import com.nexusbank.entity.Account;
import com.nexusbank.entity.User;
import com.nexusbank.enums.AccountType;
import com.nexusbank.enums.UserRole;
import com.nexusbank.enums.UserStatus;
import com.nexusbank.repository.AccountRepository;
import com.nexusbank.repository.UserRepository;
import com.nexusbank.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data loader that initializes sample data on application startup.
 * Perfect for development and testing with H2 in-memory database.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountNumberGenerator accountNumberGenerator;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("🏦 Initializing NexusBank with sample data...");
            createSampleUsers();
            log.info("✅ Sample data loaded successfully!");
        } else {
            log.info("📊 Database already has data. Skipping data loading.");
        }
    }

    private void createSampleUsers() {
        // 👤 User 1: John Doe (Regular User with Admin role for demo)
        User john = User.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .cpf("12345678901")
                .email("john@example.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .phone("(11) 98765-4321")
                .status(UserStatus.ACTIVE)
                .role(UserRole.ROLE_ADMIN)
                .loginAttempts(0)
                .build();

        User savedJohn = userRepository.save(john);
        log.info("✓ User created: {} ({})", john.getName(), john.getEmail());

        // 🏦 Account for John (Checking)
        Account johnCheckingAccount = Account.builder()
                .id(UUID.randomUUID())
                .user(savedJohn)
                .accountNumber(accountNumberGenerator.generate())
                .agency("0001")
                .balance(new BigDecimal("5000.00"))
                .type(AccountType.CHECKING)
                .build();

        accountRepository.save(johnCheckingAccount);
        log.info("  └─ Checking Account created: {} | Balance: R$ 5000.00", 
                johnCheckingAccount.getAccountNumber());

        // 👤 User 2: Jane Smith (Regular User)
        User jane = User.builder()
                .id(UUID.randomUUID())
                .name("Jane Smith")
                .cpf("98765432109")
                .email("jane@example.com")
                .passwordHash(passwordEncoder.encode("password456"))
                .dateOfBirth(LocalDate.of(1992, 8, 22))
                .phone("(11) 99999-8888")
                .status(UserStatus.ACTIVE)
                .role(UserRole.ROLE_USER)
                .loginAttempts(0)
                .build();

        User savedJane = userRepository.save(jane);
        log.info("✓ User created: {} ({})", jane.getName(), jane.getEmail());

        // 🏦 Account for Jane (Savings)
        Account janeSavingsAccount = Account.builder()
                .id(UUID.randomUUID())
                .user(savedJane)
                .accountNumber(accountNumberGenerator.generate())
                .agency("0001")
                .balance(new BigDecimal("2500.50"))
                .type(AccountType.SAVINGS)
                .build();

        accountRepository.save(janeSavingsAccount);
        log.info("  └─ Savings Account created: {} | Balance: R$ 2500.50", 
                janeSavingsAccount.getAccountNumber());

        // 👤 User 3: Bob Wilson (Support Staff)
        User bob = User.builder()
                .id(UUID.randomUUID())
                .name("Bob Wilson")
                .cpf("55555555555")
                .email("bob@example.com")
                .passwordHash(passwordEncoder.encode("password789"))
                .dateOfBirth(LocalDate.of(1988, 3, 10))
                .phone("(11) 97777-6666")
                .status(UserStatus.ACTIVE)
                .role(UserRole.ROLE_SUPPORT)
                .loginAttempts(0)
                .build();

        User savedBob = userRepository.save(bob);
        log.info("✓ User created: {} ({})", bob.getName(), bob.getEmail());

        // 🏦 Account for Bob (Checking)
        Account bobCheckingAccount = Account.builder()
                .id(UUID.randomUUID())
                .user(savedBob)
                .accountNumber(accountNumberGenerator.generate())
                .agency("0001")
                .balance(new BigDecimal("1000.00"))
                .type(AccountType.CHECKING)
                .build();

        accountRepository.save(bobCheckingAccount);
        log.info("  └─ Checking Account created: {} | Balance: R$ 1000.00", 
                bobCheckingAccount.getAccountNumber());

        log.info("");
        log.info("📋 Credentials for Testing:");
        log.info("  • Admin: john@example.com / password123 (ROLE_ADMIN)");
        log.info("  • User:  jane@example.com / password456 (ROLE_USER)");
        log.info("  • Support: bob@example.com / password789 (ROLE_SUPPORT)");
        log.info("");
        log.info("  H2 Console: http://localhost:8080/h2-console");
        log.info("");
    }
}
