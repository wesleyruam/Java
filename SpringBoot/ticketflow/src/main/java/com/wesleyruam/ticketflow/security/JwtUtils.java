package com.wesleyruam.ticketflow.security;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

@Component
public class JwtUtils {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    public String generateToken(String email) {
        return JWT.create()
            .withSubject(email)
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
            .sign(Algorithm.HMAC256(secret));
    }
    
    public String validateToken(String token) {
        DecodedJWT decoded = JWT.require(Algorithm.HMAC256(secret))
            .build()
            .verify(token);
        
        return decoded.getSubject();
    }
}