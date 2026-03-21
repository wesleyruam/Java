package com.wesleyruam.ticketflow.dto.User;

import java.time.LocalDateTime;

import com.wesleyruam.ticketflow.security.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO { // para retornar os dados sem senha
    private Long id;
    private String name;
    private String email;
    private Role role;
    private LocalDateTime createAt;
}
