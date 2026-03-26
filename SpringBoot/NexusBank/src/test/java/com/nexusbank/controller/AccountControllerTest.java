package com.nexusbank.controller;

import com.nexusbank.dto.response.AccountResponse;
import com.nexusbank.dto.response.BalanceResponse;
import com.nexusbank.entity.User;
import com.nexusbank.enums.AccountType;
import com.nexusbank.enums.UserRole;
import com.nexusbank.enums.UserStatus;
import com.nexusbank.exception.BusinessException;
import com.nexusbank.service.impl.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Account Controller Tests")
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    private UUID userId;
    private AccountResponse accountResponse;
    private BalanceResponse balanceResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        accountResponse = AccountResponse.builder()
                .id(UUID.randomUUID())
                .accountNumber("12345678")
                .agency("0001")
                .balance(new BigDecimal("5000.00"))
                .type(AccountType.CHECKING)
                .build();

        balanceResponse = BalanceResponse.builder()
                .balance(new BigDecimal("5000.00"))
                .queriedAt(Instant.parse("2026-03-25T10:00:00Z"))
                .build();
    }

    @Test
    @DisplayName("GET /accounts/me - should return account of authenticated user")
    @WithMockUser(roles = "USER")
    void testGetMyAccountSuccess() throws Exception {
        when(accountService.getAccount(any(UUID.class)))
                .thenReturn(accountResponse);

        mockMvc.perform(get("/accounts/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accountNumber").value("12345678"))
                .andExpect(jsonPath("$.data.balance").value(5000.00))
                .andExpect(jsonPath("$.data.type").value("CHECKING"));
    }

    @Test
    @DisplayName("GET /accounts/me - should fail without authentication")
    void testGetMyAccountUnauthorized() throws Exception {
        mockMvc.perform(get("/accounts/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /accounts/me/balance - should return balance of authenticated user")
    @WithMockUser(roles = "USER")
    void testGetBalanceSuccess() throws Exception {
        when(accountService.getBalance(any(UUID.class)))
                .thenReturn(balanceResponse);

        mockMvc.perform(get("/accounts/me/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance").value(5000.00))
                .andExpect(jsonPath("$.data.lastUpdate").exists());
    }

    @Test
    @DisplayName("GET /accounts/me/balance - should fail without authentication")
    void testGetBalanceUnauthorized() throws Exception {
        mockMvc.perform(get("/accounts/me/balance"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /accounts/me - should fail when account not found")
    @WithMockUser(roles = "USER")
    void testGetMyAccountNotFound() throws Exception {
        when(accountService.getAccount(any(UUID.class)))
                .thenThrow(new BusinessException("Conta não encontrada", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/accounts/me"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}
