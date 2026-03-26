package com.nexusbank.dto.response;

import com.nexusbank.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private String cpf;
    private LocalDate dateOfBirth;
    private String phone;
    private UserStatus status;
    private Instant createdAt;
}
