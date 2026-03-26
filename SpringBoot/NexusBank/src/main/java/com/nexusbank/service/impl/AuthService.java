package com.nexusbank.service.impl;

import com.nexusbank.dto.request.LoginRequest;
import com.nexusbank.dto.request.RefreshTokenRequest;
import com.nexusbank.dto.request.RegisterRequest;
import com.nexusbank.dto.response.AuthResponse;
import com.nexusbank.dto.response.UserResponse;
import com.nexusbank.entity.Account;
import com.nexusbank.entity.RefreshToken;
import com.nexusbank.entity.User;
import com.nexusbank.enums.UserRole;
import com.nexusbank.enums.UserStatus;
import com.nexusbank.exception.BusinessException;
import com.nexusbank.repository.AccountRepository;
import com.nexusbank.repository.RefreshTokenRepository;
import com.nexusbank.repository.UserRepository;
import com.nexusbank.security.JwtTokenProvider;
import com.nexusbank.util.AccountNumberGenerator;
import com.nexusbank.util.CpfUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountNumberGenerator accountNumberGenerator;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${security.max-login-attempts:5}")
    private int maxLoginAttempts;

    @Value("${security.lockout-duration-minutes:30}")
    private int lockoutDurationMinutes;

    @Value("${jwt.access-token-expiration:900000}")
    private long accessTokenExpiration;

    private static final String JWT_BLACKLIST_PREFIX = "jwt:blacklist:";

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validate CPF
        if (!CpfUtil.isValid(request.getCpf())) {
            throw BusinessException.badRequest("CPF inválido.");
        }

        // Validate age (must be >= 18)
        if (isMinor(request.getDateOfBirth())) {
            throw BusinessException.badRequest(
                    "Menores de 18 anos não podem abrir conta sem autorização de um responsável.");
        }

        // Check uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw BusinessException.conflict("E-mail já cadastrado.");
        }
        if (userRepository.existsByCpf(request.getCpf())) {
            throw BusinessException.conflict("CPF já cadastrado.");
        }

        // Create user
        User user = User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().toLowerCase().trim())
                .cpf(request.getCpf())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .dateOfBirth(request.getDateOfBirth())
                .phone(request.getPhone())
                .status(UserStatus.ACTIVE)
                .role(UserRole.ROLE_USER)
                .build();

        user = userRepository.save(user);

        // Create associated account (RF03)
        Account account = Account.builder()
                .user(user)
                .accountNumber(accountNumberGenerator.generate())
                .agency("0001")
                .build();

        accountRepository.save(account);

        log.info("New user registered: userId={}, email={}", user.getId(), user.getEmail());

        return generateAuthTokens(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> {
                    log.warn("Login attempt for non-existent email: {}", request.getEmail());
                    return BusinessException.unauthorized("Credenciais inválidas.");
                });

        // Check if permanently blocked (status != ACTIVE handled by UserDetails)
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw BusinessException.forbidden("Conta inativa. Entre em contato com o suporte.");
        }

        // Check temporary lockout
        if (user.isLocked()) {
            throw BusinessException.forbidden(
                    "Conta temporariamente bloqueada. Tente novamente mais tarde.");
        }

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            handleFailedLogin(user);
            throw BusinessException.unauthorized("Credenciais inválidas.");
        }

        // Successful login: reset attempts
        user.resetLoginAttempts();
        if (user.getStatus() == UserStatus.BLOCKED && !user.isLocked()) {
            user.unlock();
        }
        userRepository.save(user);

        log.info("Successful login: userId={}", user.getId());
        return generateAuthTokens(user);
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String tokenHash = hashToken(request.getRefreshToken());

        RefreshToken stored = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> BusinessException.unauthorized("Refresh token inválido."));

        if (!stored.isValid()) {
            // Rotate: invalidate all tokens for this user (possible token theft)
            refreshTokenRepository.revokeAllUserTokens(stored.getUser().getId());
            throw BusinessException.unauthorized("Refresh token expirado ou revogado.");
        }

        // Rotate: revoke old token and issue new pair
        stored.revoke();
        refreshTokenRepository.save(stored);

        User user = stored.getUser();
        log.info("Token rotated for userId={}", user.getId());
        return generateAuthTokens(user);
    }

    @Transactional
    public void logout(String accessToken, String userId) {
        // Blacklist the access token in Redis until it expires
        try {
            long remainingMs = jwtTokenProvider.extractAllClaims(accessToken)
                    .getExpiration().getTime() - System.currentTimeMillis();
            if (remainingMs > 0) {
                redisTemplate.opsForValue().set(
                        JWT_BLACKLIST_PREFIX + accessToken,
                        "revoked",
                        Duration.ofMillis(remainingMs)
                );
            }
        } catch (Exception e) {
            log.warn("Could not blacklist token for userId={}: {}", userId, e.getMessage());
        }

        // Revoke all refresh tokens
        refreshTokenRepository.revokeAllUserTokens(
                java.util.UUID.fromString(userId));

        log.info("User logged out: userId={}", userId);
    }

    // ─── Private helpers ─────────────────────────────────────────────────────

    private AuthResponse generateAuthTokens(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name());
        String rawRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // Persist hashed refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(rawRefreshToken))
                .expiresAt(Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpiration()))
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rawRefreshToken)
                .expiresIn(accessTokenExpiration / 1000)
                .tokenType("Bearer")
                .user(toUserResponse(user))
                .build();
    }

    private void handleFailedLogin(User user) {
        user.incrementLoginAttempts();
        if (user.getLoginAttempts() >= maxLoginAttempts) {
            user.lock(lockoutDurationMinutes);
            log.warn("Account locked after {} failed attempts: userId={}", maxLoginAttempts, user.getId());
        }
        userRepository.save(user);
    }

    private boolean isMinor(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears() < 18;
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .cpf(user.getCpf())
                .dateOfBirth(user.getDateOfBirth())
                .phone(user.getPhone())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
