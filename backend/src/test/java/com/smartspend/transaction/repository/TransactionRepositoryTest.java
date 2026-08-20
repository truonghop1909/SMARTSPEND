package com.smartspend.transaction.repository;

import com.smartspend.auth.entity.User;
import com.smartspend.auth.enums.AuthProvider;
import com.smartspend.auth.repository.UserRepository;
import com.smartspend.category.entity.Category;
import com.smartspend.category.entity.CategoryType;
import com.smartspend.category.repository.CategoryRepository;
import com.smartspend.transaction.entity.Transaction;
import com.smartspend.transaction.entity.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import com.smartspend.config.JpaAuditingConfig;
import org.springframework.context.annotation.Import;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(JpaAuditingConfig.class)
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        user = new User(
                "transaction-test@example.com",
                "password-hash",
                "Transaction Test",
                AuthProvider.LOCAL
        );

        user = userRepository.save(user);

        category = new Category(
                user,
                "Test Expense",
                CategoryType.EXPENSE,
                null,
                null,
                false
        );

        category = categoryRepository.save(category);
    }

    @Test
    void findByIdAndUserIdAndDeletedAtIsNull_shouldReturnTransaction() {
        Transaction transaction =
                new Transaction(
                        user,
                        category,
                        TransactionType.EXPENSE,
                        new BigDecimal("100000"),
                        "WinMart",
                        "CASH",
                        "Test",
                        LocalDate.now()
                );

        transaction =
                transactionRepository.save(transaction);

        Optional<Transaction> result =
                transactionRepository
                        .findByIdAndUserIdAndDeletedAtIsNull(
                                transaction.getId(),
                                user.getId()
                        );

        assertTrue(result.isPresent());

        assertEquals(
                transaction.getId(),
                result.get().getId()
        );
    }

    @Test
    void findByIdAndUserIdAndDeletedAtIsNull_shouldIgnoreSoftDeletedTransaction() {
        Transaction transaction =
                new Transaction(
                        user,
                        category,
                        TransactionType.EXPENSE,
                        new BigDecimal("100000"),
                        null,
                        null,
                        null,
                        LocalDate.now()
                );

        transaction.softDelete();

        transaction =
                transactionRepository.save(transaction);

        Optional<Transaction> result =
                transactionRepository
                        .findByIdAndUserIdAndDeletedAtIsNull(
                                transaction.getId(),
                                user.getId()
                        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdAndUserIdAndDeletedAtIsNull_shouldNotReturnAnotherUsersTransaction() {
        User anotherUser =
                new User(
                        "another@example.com",
                        "password-hash",
                        "Another User",
                        AuthProvider.LOCAL
                );

        anotherUser =
                userRepository.save(anotherUser);

        Transaction transaction =
                new Transaction(
                        user,
                        category,
                        TransactionType.EXPENSE,
                        new BigDecimal("100000"),
                        null,
                        null,
                        null,
                        LocalDate.now()
                );

        transaction =
                transactionRepository.save(transaction);

        Optional<Transaction> result =
                transactionRepository
                        .findByIdAndUserIdAndDeletedAtIsNull(
                                transaction.getId(),
                                anotherUser.getId()
                        );

        assertTrue(result.isEmpty());
    }

    @Test
    void countByCategoryIdAndDeletedAtIsNull_shouldCountOnlyActiveTransactions() {
        Transaction active =
                new Transaction(
                        user,
                        category,
                        TransactionType.EXPENSE,
                        new BigDecimal("100000"),
                        null,
                        null,
                        null,
                        LocalDate.now()
                );

        Transaction deleted =
                new Transaction(
                        user,
                        category,
                        TransactionType.EXPENSE,
                        new BigDecimal("200000"),
                        null,
                        null,
                        null,
                        LocalDate.now()
                );

        deleted.softDelete();

        transactionRepository.save(active);
        transactionRepository.save(deleted);

        long count =
                transactionRepository
                        .countByCategoryIdAndDeletedAtIsNull(
                                category.getId()
                        );

        assertEquals(1L, count);
    }
}