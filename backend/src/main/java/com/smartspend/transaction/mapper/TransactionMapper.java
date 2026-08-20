package com.smartspend.transaction.mapper;

import com.smartspend.auth.entity.User;
import com.smartspend.category.entity.Category;
import com.smartspend.transaction.dto.request.CreateTransactionRequest;
import com.smartspend.transaction.dto.request.UpdateTransactionRequest;
import com.smartspend.transaction.dto.response.TransactionResponse;
import com.smartspend.transaction.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(
            target = "categoryId",
            source = "category.id"
    )
    @Mapping(
            target = "categoryName",
            source = "category.name"
    )
    TransactionResponse toResponse(
            Transaction transaction
    );

    default Transaction toEntity(
            CreateTransactionRequest request,
            User user,
            Category category
    ) {
        Objects.requireNonNull(
                request,
                "Create transaction request must not be null"
        );

        Objects.requireNonNull(
                user,
                "User must not be null"
        );

        Objects.requireNonNull(
                category,
                "Category must not be null"
        );

        return new Transaction(
                user,
                category,
                request.type(),
                request.amount(),
                normalizeNullable(
                        request.merchant()
                ),
                normalizeNullable(
                        request.paymentMethod()
                ),
                normalizeNullable(
                        request.note()
                ),
                request.transactionDate()
        );
    }

    default void updateEntity(
            UpdateTransactionRequest request,
            Category category,
            @MappingTarget Transaction transaction
    ) {
        Objects.requireNonNull(
                request,
                "Update transaction request must not be null"
        );

        Objects.requireNonNull(
                category,
                "Category must not be null"
        );

        Objects.requireNonNull(
                transaction,
                "Transaction must not be null"
        );

        transaction.setCategory(
                category
        );

        transaction.setType(
                request.type()
        );

        transaction.setAmount(
                request.amount()
        );

        transaction.setMerchant(
                normalizeNullable(
                        request.merchant()
                )
        );

        transaction.setPaymentMethod(
                normalizeNullable(
                        request.paymentMethod()
                )
        );

        transaction.setNote(
                normalizeNullable(
                        request.note()
                )
        );

        transaction.setTransactionDate(
                request.transactionDate()
        );
    }

    private String normalizeNullable(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized =
                value.trim();

        return normalized.isBlank()
                ? null
                : normalized;
    }
}