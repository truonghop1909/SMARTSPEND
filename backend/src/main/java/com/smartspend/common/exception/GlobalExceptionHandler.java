package com.smartspend.common.exception;

import com.smartspend.common.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(
            AppException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();

        log.warn(
                "Business exception. errorCode={}, message={}",
                errorCode.getCode(),
                exception.getMessage()
        );

        return buildErrorResponse(
                errorCode,
                exception.getMessage(),
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError fieldError
                : exception.getBindingResult().getFieldErrors()) {

            errors.putIfAbsent(
                    fieldError.getField(),
                    resolveMessage(fieldError.getDefaultMessage())
            );
        }

        return buildErrorResponse(
                ErrorCode.VALIDATION_ERROR,
                ErrorCode.VALIDATION_ERROR.getMessage(),
                errors
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (ConstraintViolation<?> violation
                : exception.getConstraintViolations()) {

            String field = violation
                    .getPropertyPath()
                    .toString();

            errors.putIfAbsent(
                    field,
                    resolveMessage(violation.getMessage())
            );
        }

        return buildErrorResponse(
                ErrorCode.VALIDATION_ERROR,
                ErrorCode.VALIDATION_ERROR.getMessage(),
                errors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableRequestBody(
            HttpMessageNotReadableException exception
    ) {
        log.debug(
                "Unreadable request body. message={}",
                exception.getMessage()
        );

        return buildErrorResponse(
                ErrorCode.INVALID_REQUEST_BODY,
                ErrorCode.INVALID_REQUEST_BODY.getMessage(),
                null
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingRequestParameter(
            MissingServletRequestParameterException exception
    ) {
        String message = "Thiếu tham số bắt buộc: "
                + exception.getParameterName();

        return buildErrorResponse(
                ErrorCode.INVALID_REQUEST,
                message,
                null
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception
    ) {
        String message = "Giá trị của tham số '"
                + exception.getName()
                + "' không hợp lệ";

        return buildErrorResponse(
                ErrorCode.INVALID_REQUEST,
                message,
                null
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception
    ) {
        String message = "Phương thức HTTP '"
                + exception.getMethod()
                + "' không được hỗ trợ";

        return buildErrorResponse(
                ErrorCode.METHOD_NOT_ALLOWED,
                message,
                null
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            NoResourceFoundException exception
    ) {
        String message = "Không tìm thấy đường dẫn yêu cầu";

        return buildErrorResponse(
                ErrorCode.RESOURCE_NOT_FOUND,
                message,
                null
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException exception
    ) {
        log.warn(
                "Access denied. message={}",
                exception.getMessage()
        );

        return buildErrorResponse(
                ErrorCode.ACCESS_DENIED,
                ErrorCode.ACCESS_DENIED.getMessage(),
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(
            Exception exception
    ) {
        log.error(
                "Unexpected system error",
                exception
        );

        return buildErrorResponse(
                ErrorCode.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                null
        );
    }

    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(
            ErrorCode errorCode,
            String message,
            Map<String, String> errors
    ) {
        ApiResponse<Void> response;

        if (errors == null || errors.isEmpty()) {
            response = ApiResponse.error(
                    errorCode.getCode(),
                    message
            );
        } else {
            response = ApiResponse.error(
                    errorCode.getCode(),
                    message,
                    errors
            );
        }

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }

    private String resolveMessage(String message) {
        if (message == null || message.isBlank()) {
            return ErrorCode.VALIDATION_ERROR.getMessage();
        }

        return message;
    }
}
