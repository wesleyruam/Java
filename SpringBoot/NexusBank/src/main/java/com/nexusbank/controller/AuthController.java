package com.nexusbank.controller;

import com.nexusbank.dto.request.LoginRequest;
import com.nexusbank.dto.request.RefreshTokenRequest;
import com.nexusbank.dto.request.RegisterRequest;
import com.nexusbank.dto.response.ApiResponse;
import com.nexusbank.dto.response.AuthResponse;
import com.nexusbank.security.JwtTokenProvider;
import com.nexusbank.service.impl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Registro, login, refresh e logout")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    @Operation(summary = "Cadastrar novo usuário")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Usuário cadastrado com sucesso."));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login realizado com sucesso."));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar access token via refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Token renovado com sucesso."));
    }

    @PostMapping("/logout")
    @Operation(summary = "Encerrar sessão e revogar tokens")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader,
            @AuthenticationPrincipal UserDetails userDetails) {
        String token = extractToken(authHeader);
        String userId = jwtTokenProvider.extractUserId(token).toString();
        authService.logout(token, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Logout realizado com sucesso."));
    }

    private String extractToken(String bearerHeader) {
        if (StringUtils.hasText(bearerHeader) && bearerHeader.startsWith("Bearer ")) {
            return bearerHeader.substring(7);
        }
        return bearerHeader;
    }
}
