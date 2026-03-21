package com.wesleyruam.ticketflow.security;

import com.wesleyruam.ticketflow.model.User.UserModel;
import com.wesleyruam.ticketflow.repository.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthContext {
    
    @Autowired
    private JwtUtils jwtUtils;  // <-- INJETANDO AQUI
    
    @Autowired
    private UserRepository userRepository;
    
    public String extractToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
            .currentRequestAttributes())
            .getRequest();
        
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    public UserModel getCurrentUser() {
        String token = extractToken();
        if (token == null) {
            throw new RuntimeException("Token não fornecido");
        }
        
        // USANDO O JWT UTILS AQUI
        String email = jwtUtils.validateToken(token);
        
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
    
    public boolean hasPermission(Permission permission) {
        try {
            UserModel currentUser = getCurrentUser();
            return currentUser.hasPermission(permission);
        } catch (Exception e) {
            return false;
        }
    }
    
    public void requirePermission(Permission permission) {
        if (!hasPermission(permission)) {
            throw new SecurityException("Você não tem permissão para realizar esta ação");
        }
    }
}