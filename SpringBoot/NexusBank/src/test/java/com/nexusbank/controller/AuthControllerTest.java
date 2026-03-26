package com.nexusbank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusbank.dto.request.LoginRequest;
import com.nexusbank.dto.request.RegisterRequest;
import com.nexusbank.dto.request.RefreshTokenRequest;
import com.nexusbank.dto.response.AuthResponse;
import com.nexusbank.exception.BusinessException;
import com.nexusbank.service.impl.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .cpf("12345678901")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phone("11987654321")
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        authResponse = AuthResponse.builder()
                .accessToken("eyJhbGciOiJIUzI1NiJ9...")
                .refreshToken("refresh_token_123...")
                .tokenType("Bearer")
                .expiresIn(900L)
                .build();
    }

    @Test
    @DisplayName("POST /auth/register - should register new user successfully")
    void testRegisterSuccess() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(authResponse);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.accessToken").value("eyJhbGciOiJIUzI1NiJ9..."))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.message").value("Usuário cadastrado com sucesso."));
    }

    @Test
    @DisplayName("POST /auth/register - should fail with invalid email")
    void testRegisterInvalidEmail() throws Exception {
        registerRequest.setEmail("invalid-email");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    @DisplayName("POST /auth/login - should login successfully")
    void testLoginSuccess() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.message").value("Login realizado com sucesso."));
    }

    @Test
    @DisplayName("POST /auth/login - should fail with invalid credentials")
    void testLoginInvalidCredentials() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BusinessException("Credenciais inválidas", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("POST /auth/refresh - should refresh token successfully")
    void testRefreshTokenSuccess() throws Exception {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("refresh_token_123...")
                .build();

        when(authService.refreshToken(any(RefreshTokenRequest.class)))
                .thenReturn(authResponse);

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.message").value("Token renovado com sucesso."));
    }

    @Test
    @DisplayName("POST /auth/refresh - should fail with invalid token")
    void testRefreshInvalidToken() throws Exception {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("invalid_token")
                .build();

        when(authService.refreshToken(any(RefreshTokenRequest.class)))
                .thenThrow(new BusinessException("Token inválido", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /auth/logout - should logout successfully")
    void testLogoutSuccess() throws Exception {
        String authHeader = "Bearer eyJhbGciOiJIUzI1NiJ9...";
        
        doNothing().when(authService).logout(any(String.class), any(String.class));

        mockMvc.perform(post("/auth/logout")
                .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout realizado com sucesso."));
    }

    @Test
    @DisplayName("POST /auth/register - should fail with duplicate email")
    void testRegisterDuplicateEmail() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new BusinessException("Email já registrado", HttpStatus.CONFLICT));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }
}
