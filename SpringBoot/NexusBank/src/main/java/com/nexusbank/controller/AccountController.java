package com.nexusbank.controller;

import com.nexusbank.dto.response.AccountResponse;
import com.nexusbank.dto.response.ApiResponse;
import com.nexusbank.dto.response.BalanceResponse;
import com.nexusbank.entity.User;
import com.nexusbank.service.impl.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Tag(name = "Conta", description = "Dados e saldo da conta bancária")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/me")
    @Operation(summary = "Buscar dados da conta do usuário autenticado")
    public ResponseEntity<ApiResponse<AccountResponse>> getMyAccount(
            @AuthenticationPrincipal User principal) {
        AccountResponse account = accountService.getAccount(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(account));
    }

    @GetMapping("/me/balance")
    @Operation(summary = "Consultar saldo da conta")
    public ResponseEntity<ApiResponse<BalanceResponse>> getBalance(
            @AuthenticationPrincipal User principal) {
        BalanceResponse balance = accountService.getBalance(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(balance));
    }
}
