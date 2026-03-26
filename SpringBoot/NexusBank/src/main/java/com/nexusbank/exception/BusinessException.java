package com.nexusbank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = null;
    }

    public BusinessException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    // ─── Convenience factories ───────────────────────────────────────────────

    public static BusinessException notFound(String message) {
        return new BusinessException(message, HttpStatus.NOT_FOUND, "NOT_FOUND");
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(message, HttpStatus.CONFLICT, "CONFLICT");
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException(message, HttpStatus.FORBIDDEN, "FORBIDDEN");
    }

    public static BusinessException unauthorized(String message) {
        return new BusinessException(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }

    public static BusinessException insufficientBalance() {
        return new BusinessException(
                "Saldo insuficiente para realizar a operação.",
                HttpStatus.UNPROCESSABLE_ENTITY,
                "INSUFFICIENT_BALANCE"
        );
    }

    public static BusinessException accountBlocked() {
        return new BusinessException(
                "Conta bloqueada. Entre em contato com o suporte.",
                HttpStatus.FORBIDDEN,
                "ACCOUNT_BLOCKED"
        );
    }

    public static BusinessException duplicateTransaction() {
        return new BusinessException(
                "Transação duplicada detectada. Utilize uma chave de idempotência diferente.",
                HttpStatus.CONFLICT,
                "DUPLICATE_TRANSACTION"
        );
    }
}
