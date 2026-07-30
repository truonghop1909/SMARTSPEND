package com.smartspend.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private static final String TRACE_ID_KEY = "traceId";

    private boolean success;

    private String message;

    private T data;

    private String errorCode;

    private Map<String, String> errors;

    private LocalDateTime timestamp;

    private String traceId;

    public static <T> ApiResponse<T> success(
            String message,
            T data
    ) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .traceId(currentTraceId())
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success("Thành công", data);
    }

    public static ApiResponse<Void> success(String message) {
        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .traceId(currentTraceId())
                .build();
    }

    public static ApiResponse<Void> error(
            String errorCode,
            String message
    ) {
        return ApiResponse.<Void>builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .traceId(currentTraceId())
                .build();
    }

    public static ApiResponse<Void> error(
            String errorCode,
            String message,
            Map<String, String> errors
    ) {
        return ApiResponse.<Void>builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .traceId(currentTraceId())
                .build();
    }

    private static String currentTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }
}
