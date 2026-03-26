package com.nexusbank.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private String tokenType;
    private UserResponse user;

    public static String TOKEN_TYPE = "Bearer";
}
