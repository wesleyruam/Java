package com.wesleyruam.ticketflow.dto.User;

import com.wesleyruam.ticketflow.security.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {
    private String name;
    private String email;
    private Role role;
}
