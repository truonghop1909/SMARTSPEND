package com.smartspend.transaction.repository;

import com.smartspend.transaction.entity.Transaction;
import com.smartspend.transaction.entity.TransactionType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class TransactionSpecifications {

    private TransactionSpecifications() {
    }

    public static Specification<Transaction>
    belongsToUser(Long userId) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("user").get("id"),
                        userId
                );
    }

    public static Specification<Transaction>
    notDeleted() {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNull(
                        root.get("deletedAt")
                );
    }

    public static Specification<Transaction>
    hasType(TransactionType type) {

        if (type == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("type"),
                        type
                );
    }

    public static Specification<Transaction>
    hasCategory(Long categoryId) {

        if (categoryId == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("category").get("id"),
                        categoryId
                );
    }

    public static Specification<Transaction>
    transactionDateFrom(LocalDate fromDate) {

        if (fromDate == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("transactionDate"),
                        fromDate
                );
    }

    public static Specification<Transaction>
    transactionDateTo(LocalDate toDate) {

        if (toDate == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("transactionDate"),
                        toDate
                );
    }

    public static Specification<Transaction>
    containsKeyword(String keyword) {

        if (keyword == null
                || keyword.isBlank()) {
            return null;
        }

        String pattern =
                "%"
                        + keyword
                        .trim()
                        .toLowerCase()
                        + "%";

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("merchant")
                                ),
                                pattern
                        ),

                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("note")
                                ),
                                pattern
                        )
                );
    }

    public static Specification<Transaction> filter(
            Long userId,
            TransactionType type,
            Long categoryId,
            LocalDate fromDate,
            LocalDate toDate,
            String keyword
    ) {
        return Specification
                .where(
                        belongsToUser(userId)
                )
                .and(
                        notDeleted()
                )
                .and(
                        hasType(type)
                )
                .and(
                        hasCategory(categoryId)
                )
                .and(
                        transactionDateFrom(fromDate)
                )
                .and(
                        transactionDateTo(toDate)
                )
                .and(
                        containsKeyword(keyword)
                );
    }
}