package com.nexusbank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusbank.dto.request.LoginRequest;
import com.nexusbank.dto.request.RegisterRequest;
import com.nexusbank.dto.request.TransferRequest;
import com.nexusbank.dto.response.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("NexusBank Integration Tests")
class NexusBankIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String accessToken;
    private String userEmail = "integration-test@example.com";
    private String userPassword = "TestPassword123!@#";

    @Test
    @Order(1)
    @DisplayName("01 - Register new user")
    void testRegisterNewUser() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Integration Test User")
                .email(userEmail)
                .password(userPassword)
                .cpf("12345678901")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phone("11987654321")
                .build();

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.email").value(userEmail));
    }

    @Test
    @Order(2)
    @DisplayName("02 - Login with registered user")
    void testLoginRegisteredUser() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email(userEmail)
                .password(userPassword)
                .build();

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andReturn();

        // Extract token from response
        String response = result.getResponse().getContentAsString();
        com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(response);
        accessToken = jsonNode.get("data").get("accessToken").asText();
    }

    @Test
    @Order(3)
    @DisplayName("03 - Get authenticated user info")
    void testGetUserInfo() throws Exception {
        if (accessToken == null) {
            testLoginRegisteredUser();
        }

        mockMvc.perform(get("/users/me")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(userEmail))
                .andExpect(jsonPath("$.data.name").value("Integration Test User"));
    }

    @Test
    @Order(4)
    @DisplayName("04 - Get account info")
    void testGetAccountInfo() throws Exception {
        if (accessToken == null) {
            testLoginRegisteredUser();
        }

        mockMvc.perform(get("/accounts/me")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accountNumber").exists())
                .andExpect(jsonPath("$.data.balance").exists())
                .andExpect(jsonPath("$.data.type").value("CHECKING"));
    }

    @Test
    @Order(5)
    @DisplayName("05 - Check account balance")
    void testCheckBalance() throws Exception {
        if (accessToken == null) {
            testLoginRegisteredUser();
        }

        mockMvc.perform(get("/accounts/me/balance")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance").exists())
                .andExpect(jsonPath("$.data.lastUpdate").exists());
    }

    @Test
    @Order(6)
    @DisplayName("06 - View transaction statement (should be empty initially)")
    void testViewStatement() throws Exception {
        if (accessToken == null) {
            testLoginRegisteredUser();
        }

        mockMvc.perform(get("/transactions/statement")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    @Order(7)
    @DisplayName("07 - Make a deposit")
    void testMakeDeposit() throws Exception {
        if (accessToken == null) {
            testLoginRegisteredUser();
        }

        com.nexusbank.dto.request.DepositRequest depositRequest =
                com.nexusbank.dto.request.DepositRequest.builder()
                        .amount(new BigDecimal("1000.00"))
                        .description("Test deposit")
                        .build();

        mockMvc.perform(post("/transactions/deposit")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.amount").value(1000.00));
    }

    @Test
    @Order(8)
    @DisplayName("08 - Verify balance after deposit")
    void testBalanceAfterDeposit() throws Exception {
        if (accessToken == null) {
            testLoginRegisteredUser();
        }

        mockMvc.perform(get("/accounts/me/balance")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance").exists());
    }

    @Test
    @Order(9)
    @DisplayName("09 - Login with non-registered user should fail")
    void testLoginNonRegisteredUser() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("nonexistent@example.com")
                .password("wrongpassword")
                .build();

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(10)
    @DisplayName("10 - Access protected endpoint without token should fail")
    void testAccessProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }
}
