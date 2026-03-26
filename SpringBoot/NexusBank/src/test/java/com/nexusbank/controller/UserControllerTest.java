package com.nexusbank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusbank.dto.request.UpdateUserRequest;
import com.nexusbank.dto.response.UserResponse;
import com.nexusbank.exception.BusinessException;
import com.nexusbank.service.impl.UserService;
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

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("User Controller Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserResponse userResponse;
    private UpdateUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        userResponse = UserResponse.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john@example.com")
                .cpf("12345678901")
                .phone("11987654321")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .createdAt(Instant.parse("2026-03-25T10:00:00Z"))
                .build();

        updateRequest = UpdateUserRequest.builder()
                .name("John Updated")
                .phone("11999999999")
                .build();
    }

    @Test
    @DisplayName("GET /users/me - should return authenticated user data")
    @WithMockUser(roles = "USER")
    void testGetMeSuccess() throws Exception {
        when(userService.getUser(any(UUID.class)))
                .thenReturn(userResponse);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("john@example.com"))
                .andExpect(jsonPath("$.data.cpf").value("12345678901"));
    }

    @Test
    @DisplayName("GET /users/me - should fail without authentication")
    void testGetMeUnauthorized() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /users/me - should update user successfully")
    @WithMockUser(roles = "USER")
    void testUpdateMeSuccess() throws Exception {
        UserResponse updatedResponse = userResponse.toBuilder()
                .name("John Updated")
                .phone("11999999999")
                .build();

        when(userService.updateUser(any(UUID.class), any(UpdateUserRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("John Updated"))
                .andExpect(jsonPath("$.data.phone").value("11999999999"))
                .andExpect(jsonPath("$.message").value("Dados atualizados com sucesso."));
    }

    @Test
    @DisplayName("PUT /users/me - should fail without authentication")
    void testUpdateMeUnauthorized() throws Exception {
        mockMvc.perform(put("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /users/me - should fail with invalid phone format")
    @WithMockUser(roles = "USER")
    void testUpdateMeInvalidPhone() throws Exception {
        UpdateUserRequest invalidRequest = UpdateUserRequest.builder()
                .name("John")
                .phone("invalid")
                .build();

        mockMvc.perform(put("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /users/me - should fail when user not found")
    @WithMockUser(roles = "USER")
    void testGetMeNotFound() throws Exception {
        when(userService.getUser(any(UUID.class)))
                .thenThrow(new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}
