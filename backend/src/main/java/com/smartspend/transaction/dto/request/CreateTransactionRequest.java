package com.smartspend.transaction.dto.request;

import com.smartspend.transaction.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionRequest(

        @NotNull(message = "Category is required")
        Long categoryId,

        @NotNull(message = "Transaction type is required")
        TransactionType type,

        @NotNull(message = "Amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Amount must be greater than 0"
        )
        BigDecimal amount,

        @Size(
                max = 255,
                message = "Merchant must not exceed 255 characters"
        )
        String merchant,

        @Size(
                max = 20,
                message = "Payment method must not exceed 20 characters"
        )
        String paymentMethod,

        @Size(
                max = 500,
                message = "Note must not exceed 500 characters"
        )
        String note,

        @NotNull(message = "Transaction date is required")
        @PastOrPresent(
                message = "Transaction date must not be in the future"
        )
        LocalDate transactionDate

) {
}