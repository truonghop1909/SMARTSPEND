package com.smartspend.transaction.dto.response;

import com.smartspend.transaction.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(

        Long id,

        Long categoryId,

        String categoryName,

        TransactionType type,

        BigDecimal amount,

        String merchant,

        String paymentMethod,

        String note,

        LocalDate transactionDate,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}