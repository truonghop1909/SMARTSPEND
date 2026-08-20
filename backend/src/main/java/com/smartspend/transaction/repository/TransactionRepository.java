package com.smartspend.transaction.repository;

import com.smartspend.dashboard.projection.CategoryStatisticProjection;
import com.smartspend.dashboard.projection.MonthlyTrendProjection;
import com.smartspend.transaction.entity.Transaction;
import com.smartspend.transaction.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long>,
        JpaSpecificationExecutor<Transaction> {

    Optional<Transaction> findByIdAndUserIdAndDeletedAtIsNull(
            Long transactionId,
            Long userId
    );

    Page<Transaction> findAllByUserIdAndDeletedAtIsNull(
            Long userId,
            Pageable pageable
    );

    Page<Transaction> findAllByUserIdAndTypeAndDeletedAtIsNull(
            Long userId,
            TransactionType type,
            Pageable pageable
    );

    Page<Transaction> findAllByUserIdAndCategoryIdAndDeletedAtIsNull(
            Long userId,
            Long categoryId,
            Pageable pageable
    );

    Page<Transaction> findAllByUserIdAndTransactionDateBetweenAndDeletedAtIsNull(
            Long userId,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    );

    boolean existsByIdAndUserIdAndDeletedAtIsNull(
            Long transactionId,
            Long userId
    );

    long countByCategoryIdAndDeletedAtIsNull(
            Long categoryId
    );

    // =========================================================
    // BUDGET
    // =========================================================

    @Query("""
            select coalesce(sum(t.amount), 0)
            from Transaction t
            where t.user.id = :userId
              and t.category.id = :categoryId
              and t.type = :type
              and t.transactionDate between :fromDate and :toDate
              and t.deletedAt is null
            """)
    BigDecimal calculateAmountByCategoryAndPeriod(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("type") TransactionType type,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // =========================================================
    // DASHBOARD SUMMARY
    // =========================================================

    @Query("""
            select coalesce(sum(t.amount), 0)
            from Transaction t
            where t.user.id = :userId
              and t.type = :type
              and t.transactionDate between :fromDate and :toDate
              and t.deletedAt is null
            """)
    BigDecimal calculateTotalAmountByTypeAndPeriod(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // =========================================================
    // CATEGORY STATISTICS
    // =========================================================

    @Query("""
            select
                c.id as categoryId,
                c.name as categoryName,
                sum(t.amount) as amount
            from Transaction t
            join t.category c
            where t.user.id = :userId
              and t.type = :type
              and t.transactionDate between :fromDate and :toDate
              and t.deletedAt is null
            group by c.id, c.name
            order by sum(t.amount) desc
            """)
    List<CategoryStatisticProjection> findStatisticsByCategory(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // =========================================================
    // MONTHLY TREND
    // =========================================================

    @Query("""
            select
                year(t.transactionDate) as year,
                month(t.transactionDate) as month,
                sum(
                    case
                        when t.type = com.smartspend.transaction.entity.TransactionType.INCOME
                        then t.amount
                        else 0
                    end
                ) as totalIncome,
                sum(
                    case
                        when t.type = com.smartspend.transaction.entity.TransactionType.EXPENSE
                        then t.amount
                        else 0
                    end
                ) as totalExpense
            from Transaction t
            where t.user.id = :userId
              and t.transactionDate between :fromDate and :toDate
              and t.deletedAt is null
            group by
                year(t.transactionDate),
                month(t.transactionDate)
            order by
                year(t.transactionDate),
                month(t.transactionDate)
            """)
    List<MonthlyTrendProjection> findMonthlyTrend(
            @Param("userId") Long userId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );
}