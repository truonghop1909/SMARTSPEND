package com.smartspend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

        // =========================
        // COMMON
        // =========================

        VALIDATION_ERROR(
                        HttpStatus.BAD_REQUEST,
                        "VALIDATION_ERROR",
                        "Dữ liệu đầu vào không hợp lệ"),

        INVALID_REQUEST(
                        HttpStatus.BAD_REQUEST,
                        "INVALID_REQUEST",
                        "Yêu cầu không hợp lệ"),

        INVALID_REQUEST_BODY(
                        HttpStatus.BAD_REQUEST,
                        "INVALID_REQUEST_BODY",
                        "Nội dung yêu cầu không hợp lệ"),

        UNAUTHORIZED(
                        HttpStatus.UNAUTHORIZED,
                        "UNAUTHORIZED",
                        "Bạn chưa được xác thực"),

        ACCESS_DENIED(
                        HttpStatus.FORBIDDEN,
                        "ACCESS_DENIED",
                        "Bạn không có quyền thực hiện thao tác này"),

        RESOURCE_NOT_FOUND(
                        HttpStatus.NOT_FOUND,
                        "RESOURCE_NOT_FOUND",
                        "Không tìm thấy tài nguyên"),

        METHOD_NOT_ALLOWED(
                        HttpStatus.METHOD_NOT_ALLOWED,
                        "METHOD_NOT_ALLOWED",
                        "Phương thức HTTP không được hỗ trợ"),

        CONFLICT(
                        HttpStatus.CONFLICT,
                        "CONFLICT",
                        "Dữ liệu đang bị xung đột"),

        INTERNAL_SERVER_ERROR(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "INTERNAL_SERVER_ERROR",
                        "Đã xảy ra lỗi hệ thống"),

        // =========================
        // AUTHENTICATION
        // =========================

        AUTH_EMAIL_ALREADY_EXISTS(
                        HttpStatus.CONFLICT,
                        "AUTH_EMAIL_ALREADY_EXISTS",
                        "Email đã được sử dụng"),

        AUTH_INVALID_CREDENTIALS(
                        HttpStatus.UNAUTHORIZED,
                        "AUTH_INVALID_CREDENTIALS",
                        "Email hoặc mật khẩu không chính xác"),

        AUTH_REFRESH_TOKEN_INVALID(
                        HttpStatus.UNAUTHORIZED,
                        "AUTH_REFRESH_TOKEN_INVALID",
                        "Refresh token không hợp lệ hoặc đã hết hạn"),

        AUTH_RATE_LIMIT_EXCEEDED(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "AUTH_RATE_LIMIT_EXCEEDED",
                        "Bạn đã đăng nhập sai quá nhiều lần, vui lòng thử lại sau"),

        USER_NOT_FOUND(
                        HttpStatus.NOT_FOUND,
                        "USER_NOT_FOUND",
                        "Không tìm thấy người dùng"),
        CATEGORY_NOT_FOUND(
                        HttpStatus.NOT_FOUND,
                        "CATEGORY_NOT_FOUND",
                        "Không tìm thấy danh mục"),

        CATEGORY_ALREADY_EXISTS(
                        HttpStatus.CONFLICT,
                        "CATEGORY_ALREADY_EXISTS",
                        "Danh mục đã tồn tại"),

        CATEGORY_DEFAULT_IMMUTABLE(
                        HttpStatus.BAD_REQUEST,
                        "CATEGORY_DEFAULT_IMMUTABLE",
                        "Không thể sửa hoặc xóa danh mục mặc định"),

        CATEGORY_IN_USE(
                        HttpStatus.CONFLICT,
                        "CATEGORY_IN_USE",
                        "Không thể xóa danh mục đang được sử dụng"),
        TRANSACTION_NOT_FOUND(
                        HttpStatus.NOT_FOUND,
                        "TRANSACTION_NOT_FOUND",
                        "Không tìm thấy giao dịch"),

        TRANSACTION_INVALID_AMOUNT(
                        HttpStatus.BAD_REQUEST,
                        "TRANSACTION_INVALID_AMOUNT",
                        "Số tiền giao dịch phải lớn hơn 0"),

        TRANSACTION_FUTURE_DATE(
                        HttpStatus.BAD_REQUEST,
                        "TRANSACTION_FUTURE_DATE",
                        "Ngày giao dịch không được ở tương lai"),

        TRANSACTION_CATEGORY_TYPE_MISMATCH(
                        HttpStatus.BAD_REQUEST,
                        "TRANSACTION_CATEGORY_TYPE_MISMATCH",
                        "Loại giao dịch không khớp với loại danh mục"),

        TRANSACTION_INVALID_DATE_RANGE(
                        HttpStatus.BAD_REQUEST,
                        "TRANSACTION_INVALID_DATE_RANGE",
                        "Khoảng ngày giao dịch không hợp lệ"),

        TRANSACTION_INVALID_AMOUNT_RANGE(
                        HttpStatus.BAD_REQUEST,
                        "TRANSACTION_INVALID_AMOUNT_RANGE",
                        "Khoảng số tiền giao dịch không hợp lệ"),
        BUDGET_NOT_FOUND(
                        HttpStatus.NOT_FOUND,
                        "BUDGET_NOT_FOUND",
                        "Không tìm thấy ngân sách"),

        BUDGET_ALREADY_EXISTS(
                        HttpStatus.CONFLICT,
                        "BUDGET_ALREADY_EXISTS",
                        "Ngân sách cho danh mục và tháng này đã tồn tại"),

        BUDGET_INVALID_AMOUNT(
                        HttpStatus.BAD_REQUEST,
                        "BUDGET_INVALID_AMOUNT",
                        "Số tiền ngân sách phải lớn hơn 0"),

        BUDGET_CATEGORY_MUST_BE_EXPENSE(
                        HttpStatus.BAD_REQUEST,
                        "BUDGET_CATEGORY_MUST_BE_EXPENSE",
                        "Ngân sách chỉ được áp dụng cho danh mục chi tiêu"),

        BUDGET_INVALID_PERIOD(
                        HttpStatus.BAD_REQUEST,
                        "BUDGET_INVALID_PERIOD",
                        "Kỳ ngân sách không hợp lệ"),
        NOTIFICATION_NOT_FOUND(
                        HttpStatus.NOT_FOUND,
                        "NOTIFICATION_NOT_FOUND",
                        "Không tìm thấy thông báo");

        private final HttpStatus status;
        private final String code;
        private final String message;
}