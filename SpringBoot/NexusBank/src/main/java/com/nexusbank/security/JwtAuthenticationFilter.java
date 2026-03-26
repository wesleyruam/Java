package com.nexusbank.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusbank.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // ✅ CORREÇÃO CORS — permitir preflight
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractTokenFromRequest(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtTokenProvider.validateToken(token)) {
            sendUnauthorizedError(response, "Token JWT inválido ou expirado.");
            return;
        }

        // Check if token is blacklisted (logged out)
        if (isTokenBlacklisted(token)) {
            sendUnauthorizedError(response, "Token revogado. Faça login novamente.");
            return;
        }

        // Only accept access tokens in Authorization header
        if (!jwtTokenProvider.isAccessToken(token)) {
            sendUnauthorizedError(response, "Tipo de token inválido.");
            return;
        }

        try {

            String email = jwtTokenProvider.extractEmail(token);

            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                if (userDetails.isEnabled()
                        && userDetails.isAccountNonLocked()) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authToken);
                }
            }

        } catch (Exception e) {

            log.error(
                    "Error setting user authentication in security context: {}",
                    e.getMessage()
            );

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }


    private String extractTokenFromRequest(HttpServletRequest request) {

        String bearerToken =
                request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken)
                && bearerToken.startsWith(BEARER_PREFIX)) {

            return bearerToken.substring(
                    BEARER_PREFIX.length()
            );
        }

        return null;
    }


    private boolean isTokenBlacklisted(String token) {

        try {

            Boolean exists =
                    redisTemplate.hasKey(
                            BLACKLIST_PREFIX + token
                    );

            return Boolean.TRUE.equals(exists);

        } catch (Exception e) {

            log.warn(
                    "Redis unavailable for blacklist check, allowing token: {}",
                    e.getMessage()
            );

            return false;
        }
    }


    private void sendUnauthorizedError(
            HttpServletResponse response,
            String message
    ) throws IOException {

        response.setStatus(
                HttpStatus.UNAUTHORIZED.value()
        );

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );

        response.setCharacterEncoding("UTF-8");

        ErrorResponse error =
                ErrorResponse.builder()
                        .timestamp(Instant.now())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error("Unauthorized")
                        .message(message)
                        .build();

        objectMapper.writeValue(
                response.getWriter(),
                error
        );
    }
}