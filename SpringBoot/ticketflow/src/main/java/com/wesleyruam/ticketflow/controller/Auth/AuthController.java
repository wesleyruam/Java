package com.wesleyruam.ticketflow.controller.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wesleyruam.ticketflow.dto.ServiceResponse;
import com.wesleyruam.ticketflow.dto.Auth.LoginRequestDTO;
import com.wesleyruam.ticketflow.dto.Auth.LoginResponseDTO;
import com.wesleyruam.ticketflow.service.Auth.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<ServiceResponse<LoginResponseDTO>> login(
            @RequestBody LoginRequestDTO loginDTO) {

        ServiceResponse<LoginResponseDTO> response =
                authService.login(loginDTO);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

}