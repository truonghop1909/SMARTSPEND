package com.smartspend.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.smartspend.common.exception.ErrorCode;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        String errorCode,
        Map<String, String> errors,
        LocalDateTime timestamp,
        String traceId
) {

    private static final String TRACE_ID_KEY = "traceId";

    public static <T> ApiResponse<T> success(
            String message,
            T data
    ) {
        return new ApiResponse<>(
                true,
                message,
                data,
                null,
                null,
                LocalDateTime.now(),
                currentTraceId()
        );
    }

    public static <T> ApiResponse<T> success(T data) {
        return success("Thành công", data);
    }

    public static ApiResponse<Void> success(String message) {
        return success(message, null);
    }

    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return error(
                errorCode,
                errorCode.getMessage(),
                null
        );
    }

    public static ApiResponse<Void> error(
            ErrorCode errorCode,
            String message
    ) {
        return error(errorCode, message, null);
    }

    public static ApiResponse<Void> error(
            ErrorCode errorCode,
            String message,
            Map<String, String> errors
    ) {
        return new ApiResponse<>(
                false,
                message,
                null,
                errorCode.getCode(),
                errors,
                LocalDateTime.now(),
                currentTraceId()
        );
    }

    private static String currentTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }
}