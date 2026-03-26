package com.nexusbank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusbank.dto.request.DepositRequest;
import com.nexusbank.dto.request.PaymentRequest;
import com.nexusbank.dto.request.TransferRequest;
import com.nexusbank.dto.response.PagedResponse;
import com.nexusbank.dto.response.TransactionResponse;
import com.nexusbank.enums.TransactionStatus;
import com.nexusbank.enums.TransactionType;
import com.nexusbank.exception.BusinessException;
import com.nexusbank.service.impl.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Transaction Controller Tests")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    private TransactionResponse transactionResponse;
    private TransferRequest transferRequest;
    private DepositRequest depositRequest;
    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        transactionResponse = TransactionResponse.builder()
                .id(UUID.randomUUID())
                .sourceAccountNumber("12345678")
                .destinationAccountNumber("87654321")
                .description("Transfer test")
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .createdAt(Instant.now())
                .build();

        transferRequest = TransferRequest.builder()
                .destinationAccountNumber("87654321")
                .amount(new BigDecimal("100.00"))
                .description("Transfer test")
                .idempotencyKey("test-key-123")
                .build();

        depositRequest = DepositRequest.builder()
                .amount(new BigDecimal("500.00"))
                .description("Deposit test")
                .build();

        paymentRequest = PaymentRequest.builder()
                .barCode("12345.67890 12345.678901 12345.678901 1 12345678901234")
                .amount(new BigDecimal("150.00"))
                .idempotencyKey("test-payment-key")
                .build();
    }

    @Test
    @DisplayName("POST /transactions/transfer - should transfer successfully")
    @WithMockUser(roles = "USER")
    void testTransferSuccess() throws Exception {
        when(transactionService.transfer(any(UUID.class), any(TransferRequest.class)))
                .thenReturn(transactionResponse);

        mockMvc.perform(post("/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("TRANSFER"))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.amount").value(100.00))
                .andExpect(jsonPath("$.message").value("Transferência realizada com sucesso."));
    }

    @Test
    @DisplayName("POST /transactions/transfer - should fail with insufficient balance")
    @WithMockUser(roles = "USER")
    void testTransferInsufficientBalance() throws Exception {
        when(transactionService.transfer(any(UUID.class), any(TransferRequest.class)))
                .thenThrow(new BusinessException("Saldo insuficiente", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @DisplayName("POST /transactions/transfer - should fail with invalid destination")
    @WithMockUser(roles = "USER")
    void testTransferInvalidDestination() throws Exception {
        when(transactionService.transfer(any(UUID.class), any(TransferRequest.class)))
                .thenThrow(new BusinessException("Conta destino não encontrada", HttpStatus.NOT_FOUND));

        mockMvc.perform(post("/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /transactions/deposit - should deposit successfully")
    @WithMockUser(roles = "USER")
    void testDepositSuccess() throws Exception {
        TransactionResponse depositResponse = transactionResponse.toBuilder()
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("500.00"))
                .build();

        when(transactionService.deposit(any(UUID.class), any(DepositRequest.class)))
                .thenReturn(depositResponse);

        mockMvc.perform(post("/transactions/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.data.amount").value(500.00))
                .andExpect(jsonPath("$.message").value("Depósito realizado com sucesso."));
    }

    @Test
    @DisplayName("POST /transactions/deposit - should fail with invalid amount")
    @WithMockUser(roles = "USER")
    void testDepositInvalidAmount() throws Exception {
        DepositRequest invalidRequest = DepositRequest.builder()
                .amount(new BigDecimal("0"))
                .description("Invalid")
                .build();

        mockMvc.perform(post("/transactions/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /transactions/pay - should pay bill successfully")
    @WithMockUser(roles = "USER")
    void testPayBillSuccess() throws Exception {
        TransactionResponse paymentResponse = transactionResponse.toBuilder()
                .type(TransactionType.PAYMENT)
                .amount(new BigDecimal("150.00"))
                .build();

        when(transactionService.payBill(any(UUID.class), any(PaymentRequest.class)))
                .thenReturn(paymentResponse);

        mockMvc.perform(post("/transactions/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("PAYMENT"))
                .andExpect(jsonPath("$.data.amount").value(150.00))
                .andExpect(jsonPath("$.message").value("Pagamento realizado com sucesso."));
    }

    @Test
    @DisplayName("POST /transactions/pay - should fail with invalid barcode")
    @WithMockUser(roles = "USER")
    void testPayBillInvalidBarcode() throws Exception {
        PaymentRequest invalidRequest = PaymentRequest.builder()
                .barCode("invalid")
                .amount(new BigDecimal("150.00"))
                .build();

        when(transactionService.payBill(any(UUID.class), any(PaymentRequest.class)))
                .thenThrow(new BusinessException("Código de barras inválido", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/transactions/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /transactions/statement - should return paginated statement")
    @WithMockUser(roles = "USER")
    void testGetStatementSuccess() throws Exception {
        PagedResponse<TransactionResponse> response = PagedResponse.<TransactionResponse>builder()
                .data(List.of(transactionResponse))
                .page(0)
                .size(20)
                .totalElements(1L)
                .totalPages(1)
                .build();

        when(transactionService.getStatement(any(UUID.class), isNull(), isNull(), isNull(), eq(0), eq(20)))
                .thenReturn(response);

        mockMvc.perform(get("/transactions/statement"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].type").value("TRANSFER"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /transactions/statement - should filter by transaction type")
    @WithMockUser(roles = "USER")
    void testGetStatementFilterByType() throws Exception {
        PagedResponse<TransactionResponse> response = PagedResponse.<TransactionResponse>builder()
                .data(List.of(transactionResponse))
                .page(0)
                .size(20)
                .totalElements(1L)
                .totalPages(1)
                .build();

        when(transactionService.getStatement(any(UUID.class), isNull(), isNull(), any(), eq(0), eq(20)))
                .thenReturn(response);

        mockMvc.perform(get("/transactions/statement?type=TRANSFER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("POST /transactions/transfer - should fail without authentication")
    void testTransferUnauthorized() throws Exception {
        mockMvc.perform(post("/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /transactions/statement - should fail without authentication")
    void testGetStatementUnauthorized() throws Exception {
        mockMvc.perform(get("/transactions/statement"))
                .andExpect(status().isUnauthorized());
    }
}
