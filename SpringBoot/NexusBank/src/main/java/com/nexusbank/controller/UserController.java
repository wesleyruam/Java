package com.nexusbank.controller;

import com.nexusbank.dto.request.UpdateUserRequest;
import com.nexusbank.dto.response.ApiResponse;
import com.nexusbank.dto.response.UserResponse;
import com.nexusbank.entity.User;
import com.nexusbank.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de dados do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Buscar dados do usuário autenticado")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(
            @AuthenticationPrincipal User principal) {
        UserResponse user = userService.getUser(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PutMapping("/me")
    @Operation(summary = "Atualizar dados do usuário autenticado")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(
            @AuthenticationPrincipal User principal,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = userService.updateUser(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(user, "Dados atualizados com sucesso."));
    }
}
