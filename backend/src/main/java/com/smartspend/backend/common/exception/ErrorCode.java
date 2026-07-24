package com.smartspend.backend.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation error"),
    WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "Wallet not found");

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ErrorCode(HttpStatus httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
