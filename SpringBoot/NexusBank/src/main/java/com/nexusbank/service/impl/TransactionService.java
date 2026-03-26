package com.nexusbank.service.impl;

import com.nexusbank.dto.request.DepositRequest;
import com.nexusbank.dto.request.PaymentRequest;
import com.nexusbank.dto.request.TransferRequest;
import com.nexusbank.dto.response.PagedResponse;
import com.nexusbank.dto.response.TransactionResponse;
import com.nexusbank.entity.Account;
import com.nexusbank.entity.Bill;
import com.nexusbank.entity.Transaction;
import com.nexusbank.enums.BillStatus;
import com.nexusbank.enums.TransactionStatus;
import com.nexusbank.enums.TransactionType;
import com.nexusbank.exception.BusinessException;
import com.nexusbank.repository.AccountRepository;
import com.nexusbank.repository.BillRepository;
import com.nexusbank.repository.TransactionRepository;
import com.nexusbank.util.IdempotencyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BillRepository billRepository;
    private final IdempotencyUtil idempotencyUtil;

    // ─── Transfer ────────────────────────────────────────────────────────────

    /**
     * SERIALIZABLE isolation prevents double-spending via phantom reads.
     * Pessimistic write locks on both accounts prevent concurrent balance corruption.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransactionResponse transfer(UUID userId, TransferRequest request) {
        // Idempotency check
        if (idempotencyUtil.isDuplicate(request.getIdempotencyKey())) {
            return transactionRepository.findByReferenceId(request.getIdempotencyKey())
                    .map(this::toResponse)
                    .orElseThrow(BusinessException::duplicateTransaction);
        }

        // Acquire pessimistic locks in deterministic order (by account number) to prevent deadlock
        Account sourceAccount = accountRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> BusinessException.notFound("Conta de origem não encontrada."));

        Account destinationAccount = accountRepository
                .findByAccountNumberWithLock(request.getDestinationAccountNumber())
                .orElseThrow(() -> BusinessException.notFound("Conta de destino não encontrada."));

        // Business rules
        if (sourceAccount.getId().equals(destinationAccount.getId())) {
            throw BusinessException.badRequest("Não é possível transferir para a própria conta.");
        }

        if (!sourceAccount.hasSufficientBalance(request.getAmount())) {
            throw BusinessException.insufficientBalance();
        }

        // Create transaction record (PENDING)
        Transaction transaction = Transaction.builder()
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .description(request.getDescription())
                .referenceId(request.getIdempotencyKey())
                .build();
        transaction = transactionRepository.save(transaction);

        try {
            // Execute the financial movement
            sourceAccount.debit(request.getAmount());
            destinationAccount.credit(request.getAmount());

            accountRepository.save(sourceAccount);
            accountRepository.save(destinationAccount);

            transaction.complete();
            transaction = transactionRepository.save(transaction);

            log.info("Transfer completed: txId={}, from={}, to={}, amount={}",
                    transaction.getId(),
                    sourceAccount.getAccountNumber(),
                    destinationAccount.getAccountNumber(),
                    request.getAmount());

        } catch (Exception e) {
            transaction.fail();
            transactionRepository.save(transaction);
            log.error("Transfer failed: txId={}, error={}", transaction.getId(), e.getMessage());
            throw e;
        }

        return toResponse(transaction);
    }

    // ─── Deposit ─────────────────────────────────────────────────────────────

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TransactionResponse deposit(UUID userId, DepositRequest request) {
        Account account = accountRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> BusinessException.notFound("Conta não encontrada."));

        Transaction transaction = Transaction.builder()
                .destinationAccount(account)
                .amount(request.getAmount())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.PENDING)
                .description(request.getDescription() != null
                        ? request.getDescription() : "Depósito")
                .referenceId(UUID.randomUUID().toString())
                .build();
        transaction = transactionRepository.save(transaction);

        try {
            account.credit(request.getAmount());
            accountRepository.save(account);

            transaction.complete();
            transaction = transactionRepository.save(transaction);

            log.info("Deposit completed: txId={}, account={}, amount={}",
                    transaction.getId(), account.getAccountNumber(), request.getAmount());

        } catch (Exception e) {
            transaction.fail();
            transactionRepository.save(transaction);
            throw e;
        }

        return toResponse(transaction);
    }

    // ─── Bill Payment ─────────────────────────────────────────────────────────

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransactionResponse payBill(UUID userId, PaymentRequest request) {
        // Idempotency check
        if (idempotencyUtil.isDuplicate(request.getIdempotencyKey())) {
            return transactionRepository.findByReferenceId(request.getIdempotencyKey())
                    .map(this::toResponse)
                    .orElseThrow(BusinessException::duplicateTransaction);
        }

        Account account = accountRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> BusinessException.notFound("Conta não encontrada."));

        if (!account.hasSufficientBalance(request.getAmount())) {
            throw BusinessException.insufficientBalance();
        }

        Transaction transaction = Transaction.builder()
                .sourceAccount(account)
                .amount(request.getAmount())
                .type(TransactionType.PAYMENT)
                .status(TransactionStatus.PENDING)
                .description("Pagamento de boleto: " + maskBarCode(request.getBarCode()))
                .referenceId(request.getIdempotencyKey())
                .build();
        transaction = transactionRepository.save(transaction);

        try {
            account.debit(request.getAmount());
            accountRepository.save(account);

            // Persist bill record
            Bill bill = Bill.builder()
                    .account(account)
                    .barCode(request.getBarCode())
                    .amount(request.getAmount())
                    .dueDate(LocalDate.now())
                    .status(BillStatus.PAID)
                    .paidAt(Instant.now())
                    .build();
            billRepository.save(bill);

            transaction.complete();
            transaction = transactionRepository.save(transaction);

            log.info("Bill payment completed: txId={}, account={}, amount={}",
                    transaction.getId(), account.getAccountNumber(), request.getAmount());

        } catch (Exception e) {
            transaction.fail();
            transactionRepository.save(transaction);
            throw e;
        }

        return toResponse(transaction);
    }

    // ─── Statement ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PagedResponse<TransactionResponse> getStatement(
            UUID userId,
            Instant startDate,
            Instant endDate,
            TransactionType type,
            int page,
            int size
    ) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> BusinessException.notFound("Conta não encontrada."));

        if (size > 100) size = 100; // cap page size

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Transaction> txPage = transactionRepository.findStatement(
                account.getId(), startDate, endDate, type, pageable);

        return PagedResponse.<TransactionResponse>builder()
                .data(txPage.getContent().stream().map(this::toResponse).toList())
                .page(txPage.getNumber())
                .size(txPage.getSize())
                .totalElements(txPage.getTotalElements())
                .totalPages(txPage.getTotalPages())
                .build();
    }

    // ─── Mapping ──────────────────────────────────────────────────────────────

    private TransactionResponse toResponse(Transaction tx) {
        return TransactionResponse.builder()
                .id(tx.getId())
                .amount(tx.getAmount())
                .type(tx.getType())
                .status(tx.getStatus())
                .description(tx.getDescription())
                .referenceId(tx.getReferenceId())
                .sourceAccountNumber(tx.getSourceAccount() != null
                        ? tx.getSourceAccount().getAccountNumber() : null)
                .destinationAccountNumber(tx.getDestinationAccount() != null
                        ? tx.getDestinationAccount().getAccountNumber() : null)
                .createdAt(tx.getCreatedAt())
                .build();
    }

    private String maskBarCode(String barCode) {
        if (barCode == null || barCode.length() < 8) return "****";
        return barCode.substring(0, 4) + "****" + barCode.substring(barCode.length() - 4);
    }
}
