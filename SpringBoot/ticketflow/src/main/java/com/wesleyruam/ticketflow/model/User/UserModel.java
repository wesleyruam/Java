package com.wesleyruam.ticketflow.model.User;


import java.time.LocalDateTime;

import com.wesleyruam.ticketflow.security.Permission;
import com.wesleyruam.ticketflow.security.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    private String password;
    
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }
    
    // Método prático para verificar permissão
    public boolean hasPermission(Permission permission) {
        return this.role.hasPermission(permission);
    }
}