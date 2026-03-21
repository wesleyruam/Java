package com.wesleyruam.ticketflow.dto.Auth;

import com.wesleyruam.ticketflow.security.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private String token;
    private Long userId;
    private String name;
    private String email;
    private Role role;
}