package com.smartspend.transaction.dto.request;

import com.smartspend.transaction.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionFilterRequest(

        TransactionType type,

        Long categoryId,

        LocalDate fromDate,

        LocalDate toDate,

        @DecimalMin(
                value = "0.00",
                message = "Minimum amount must not be negative"
        )
        BigDecimal minAmount,

        @DecimalMin(
                value = "0.00",
                message = "Maximum amount must not be negative"
        )
        BigDecimal maxAmount,

        @Size(
                max = 255,
                message = "Keyword must not exceed 255 characters"
        )
        String keyword,

        @Min(
                value = 0,
                message = "Page must be greater than or equal to 0"
        )
        Integer page,

        @Min(
                value = 1,
                message = "Page size must be at least 1"
        )
        @Max(
                value = 100,
                message = "Page size must not exceed 100"
        )
        Integer size,

        String sortBy,

        String sortDirection

) {

    public int resolvedPage() {
        return page != null
                ? page
                : 0;
    }

    public int resolvedSize() {
        return size != null
                ? size
                : 20;
    }

    public String resolvedSortBy() {
        return sortBy != null
                && !sortBy.isBlank()
                ? sortBy
                : "transactionDate";
    }

    public String resolvedSortDirection() {
        return sortDirection != null
                && !sortDirection.isBlank()
                ? sortDirection
                : "desc";
    }
}