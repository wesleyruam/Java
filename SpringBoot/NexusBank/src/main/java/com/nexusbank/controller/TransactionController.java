package com.nexusbank.controller;

import com.nexusbank.dto.request.DepositRequest;
import com.nexusbank.dto.request.PaymentRequest;
import com.nexusbank.dto.request.TransferRequest;
import com.nexusbank.dto.response.ApiResponse;
import com.nexusbank.dto.response.PagedResponse;
import com.nexusbank.dto.response.TransactionResponse;
import com.nexusbank.entity.User;
import com.nexusbank.enums.TransactionType;
import com.nexusbank.service.impl.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transações", description = "Transferências, depósitos, pagamentos e extrato")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    @Operation(summary = "Realizar transferência entre contas")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @AuthenticationPrincipal User principal,
            @Valid @RequestBody TransferRequest request) {
        TransactionResponse tx = transactionService.transfer(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(tx, "Transferência realizada com sucesso."));
    }

    @PostMapping("/deposit")
    @Operation(summary = "Realizar depósito na própria conta")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @AuthenticationPrincipal User principal,
            @Valid @RequestBody DepositRequest request) {
        TransactionResponse tx = transactionService.deposit(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(tx, "Depósito realizado com sucesso."));
    }

    @PostMapping("/pay")
    @Operation(summary = "Pagar boleto / conta")
    public ResponseEntity<ApiResponse<TransactionResponse>> pay(
            @AuthenticationPrincipal User principal,
            @Valid @RequestBody PaymentRequest request) {
        TransactionResponse tx = transactionService.payBill(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(tx, "Pagamento realizado com sucesso."));
    }

    @GetMapping("/statement")
    @Operation(summary = "Consultar extrato financeiro com filtros e paginação")
    public ResponseEntity<PagedResponse<TransactionResponse>> getStatement(
            @AuthenticationPrincipal User principal,

            @Parameter(description = "Data inicial (yyyy-MM-dd)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @Parameter(description = "Data final (yyyy-MM-dd)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @Parameter(description = "Tipo de transação: TRANSFER, DEPOSIT, PAYMENT, WITHDRAWAL")
            @RequestParam(required = false)
            TransactionType type,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Instant start = startDate != null ? startDate.atStartOfDay().toInstant(ZoneOffset.UTC) : null;
        Instant end   = endDate   != null ? endDate.atTime(23, 59, 59).toInstant(ZoneOffset.UTC) : null;

        PagedResponse<TransactionResponse> result =
                transactionService.getStatement(principal.getId(), start, end, type, page, size);

        return ResponseEntity.ok(result);
    }
}
