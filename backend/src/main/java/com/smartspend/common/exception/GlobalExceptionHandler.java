package com.smartspend.common.exception;

import com.smartspend.common.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

        ApiResponse<Void> response = ApiResponse.error(
                errorCode,
                exception.getMessage()
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError fieldError
                : exception.getBindingResult().getFieldErrors()) {

            errors.putIfAbsent(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }

        ApiResponse<Void> response = ApiResponse.error(
                ErrorCode.VALIDATION_ERROR,
                ErrorCode.VALIDATION_ERROR.getMessage(),
                errors
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (ConstraintViolation<?> violation
                : exception.getConstraintViolations()) {

            errors.put(
                    violation.getPropertyPath().toString(),
                    violation.getMessage()
            );
        }

        ApiResponse<Void> response = ApiResponse.error(
                ErrorCode.VALIDATION_ERROR,
                ErrorCode.VALIDATION_ERROR.getMessage(),
                errors
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableBody(
            HttpMessageNotReadableException exception
    ) {
        ApiResponse<Void> response = ApiResponse.error(
                ErrorCode.INVALID_REQUEST_BODY
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParameter(
            MissingServletRequestParameterException exception
    ) {
        String message = "Thiếu tham số bắt buộc: "
                + exception.getParameterName();

        ApiResponse<Void> response = ApiResponse.error(
                ErrorCode.BAD_REQUEST,
                message
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception
    ) {
        String message = "Giá trị của tham số '"
                + exception.getName()
                + "' không hợp lệ";

        ApiResponse<Void> response = ApiResponse.error(
                ErrorCode.BAD_REQUEST,
                message
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(
            Exception exception
    ) {
        log.error("Unexpected system error", exception);

        ApiResponse<Void> response = ApiResponse.error(
                ErrorCode.INTERNAL_SERVER_ERROR
        );

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(response);
    }
}