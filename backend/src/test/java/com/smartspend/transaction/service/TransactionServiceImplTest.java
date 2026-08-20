package com.smartspend.transaction.service;

import com.smartspend.auth.entity.User;
import com.smartspend.auth.enums.AuthProvider;
import com.smartspend.auth.repository.UserRepository;
import com.smartspend.budget.service.BudgetService;
import com.smartspend.category.entity.Category;
import com.smartspend.category.entity.CategoryType;
import com.smartspend.category.repository.CategoryRepository;
import com.smartspend.common.exception.AppException;
import com.smartspend.dashboard.cache.DashboardCacheService;
import com.smartspend.security.UserPrincipal;
import com.smartspend.transaction.dto.request.CreateTransactionRequest;
import com.smartspend.transaction.dto.request.TransactionFilterRequest;
import com.smartspend.transaction.dto.request.UpdateTransactionRequest;
import com.smartspend.transaction.dto.response.TransactionResponse;
import com.smartspend.transaction.entity.Transaction;
import com.smartspend.transaction.entity.TransactionType;
import com.smartspend.transaction.mapper.TransactionMapper;
import com.smartspend.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private BudgetService budgetService;

    @Mock
    private DashboardCacheService dashboardCacheService;

    private TransactionServiceImpl transactionService;

    private User user;
    private Category expenseCategory;

    @BeforeEach
    void setUp() {
        transactionService =
                new TransactionServiceImpl(
                        transactionRepository,
                        categoryRepository,
                        userRepository,
                        transactionMapper,
                        redisTemplate,
                        budgetService,
                        dashboardCacheService
                );

        user =
                new User(
                        "user@example.com",
                        "password-hash",
                        "Nguyen Van A",
                        AuthProvider.LOCAL
                );

        ReflectionTestUtils.setField(
                user,
                "id",
                1L
        );

        expenseCategory =
                new Category(
                        user,
                        "Ăn uống",
                        CategoryType.EXPENSE,
                        null,
                        null,
                        false
                );

        ReflectionTestUtils.setField(
                expenseCategory,
                "id",
                10L
        );

        UserPrincipal principal =
                UserPrincipal.from(user);

        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        principal,
                        null,
                        principal.getAuthorities()
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void create_shouldSaveTransaction_whenRequestIsValid() {
        LocalDate transactionDate =
                LocalDate.now();

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        10L,
                        TransactionType.EXPENSE,
                        new BigDecimal("150000"),
                        "Highlands",
                        "CASH",
                        "Cafe",
                        transactionDate
                );

        Transaction transaction =
                new Transaction(
                        user,
                        expenseCategory,
                        TransactionType.EXPENSE,
                        new BigDecimal("150000"),
                        "Highlands",
                        "CASH",
                        "Cafe",
                        transactionDate
                );

        ReflectionTestUtils.setField(
                transaction,
                "id",
                100L
        );

        TransactionResponse expected =
                new TransactionResponse(
                        100L,
                        10L,
                        "Ăn uống",
                        TransactionType.EXPENSE,
                        new BigDecimal("150000"),
                        "Highlands",
                        "CASH",
                        "Cafe",
                        transactionDate,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(
                userRepository.findById(1L)
        ).thenReturn(
                Optional.of(user)
        );

        when(
                categoryRepository.findAccessibleById(
                        10L,
                        1L
                )
        ).thenReturn(
                Optional.of(expenseCategory)
        );

        when(
                transactionMapper.toEntity(
                        request,
                        user,
                        expenseCategory
                )
        ).thenReturn(
                transaction
        );

        when(
                transactionRepository.save(transaction)
        ).thenReturn(
                transaction
        );

        when(
                transactionMapper.toResponse(transaction)
        ).thenReturn(
                expected
        );

        TransactionResponse result =
                transactionService.create(
                        request
                );

        assertNotNull(result);

        assertEquals(
                new BigDecimal("150000"),
                result.amount()
        );

        verify(transactionRepository)
                .save(transaction);

        verify(budgetService)
                .checkBudgetAlert(
                        1L,
                        10L,
                        transactionDate
                );

        verify(dashboardCacheService)
                .evict(
                        1L,
                        transactionDate.getYear(),
                        transactionDate.getMonthValue()
                );
    }

    @Test
    void create_shouldRejectInvalidAmount() {
        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        10L,
                        TransactionType.EXPENSE,
                        BigDecimal.ZERO,
                        null,
                        null,
                        null,
                        LocalDate.now()
                );

        assertThrows(
                AppException.class,
                () ->
                        transactionService.create(
                                request
                        )
        );

        verify(
                transactionRepository,
                never()
        ).save(any());

        verify(
                budgetService,
                never()
        ).checkBudgetAlert(
                anyLong(),
                anyLong(),
                any(LocalDate.class)
        );

        verifyNoInteractions(
                dashboardCacheService
        );
    }

    @Test
    void create_shouldRejectFutureDate() {
        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        10L,
                        TransactionType.EXPENSE,
                        new BigDecimal("100000"),
                        null,
                        null,
                        null,
                        LocalDate.now().plusDays(1)
                );

        assertThrows(
                AppException.class,
                () ->
                        transactionService.create(
                                request
                        )
        );

        verify(
                transactionRepository,
                never()
        ).save(any());

        verify(
                budgetService,
                never()
        ).checkBudgetAlert(
                anyLong(),
                anyLong(),
                any(LocalDate.class)
        );

        verifyNoInteractions(
                dashboardCacheService
        );
    }

    @Test
    void create_shouldRejectCategoryTypeMismatch() {
        Category incomeCategory =
                new Category(
                        null,
                        "Lương",
                        CategoryType.INCOME,
                        null,
                        null,
                        true
                );

        ReflectionTestUtils.setField(
                incomeCategory,
                "id",
                20L
        );

        CreateTransactionRequest request =
                new CreateTransactionRequest(
                        20L,
                        TransactionType.EXPENSE,
                        new BigDecimal("100000"),
                        null,
                        null,
                        null,
                        LocalDate.now()
                );

        when(
                userRepository.findById(1L)
        ).thenReturn(
                Optional.of(user)
        );

        when(
                categoryRepository.findAccessibleById(
                        20L,
                        1L
                )
        ).thenReturn(
                Optional.of(incomeCategory)
        );

        assertThrows(
                AppException.class,
                () ->
                        transactionService.create(
                                request
                        )
        );

        verify(
                transactionRepository,
                never()
        ).save(any());

        verify(
                budgetService,
                never()
        ).checkBudgetAlert(
                anyLong(),
                anyLong(),
                any(LocalDate.class)
        );

        verifyNoInteractions(
                dashboardCacheService
        );
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void getById_shouldReturnTransaction_whenOwnedByCurrentUser() {
        Transaction transaction =
                createTransaction();

        TransactionResponse response =
                createResponse();

        when(
                transactionRepository
                        .findByIdAndUserIdAndDeletedAtIsNull(
                                100L,
                                1L
                        )
        ).thenReturn(
                Optional.of(transaction)
        );

        when(
                transactionMapper.toResponse(
                        transaction
                )
        ).thenReturn(
                response
        );

        TransactionResponse result =
                transactionService.getById(
                        100L
                );

        assertEquals(
                100L,
                result.id()
        );
    }

    @Test
    void getById_shouldThrowException_whenTransactionNotFound() {
        when(
                transactionRepository
                        .findByIdAndUserIdAndDeletedAtIsNull(
                                999L,
                                1L
                        )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                AppException.class,
                () ->
                        transactionService.getById(
                                999L
                        )
        );
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void update_shouldUpdateTransaction_whenRequestIsValid() {
        LocalDate transactionDate =
                LocalDate.now();

        Transaction transaction =
                createTransaction(
                        transactionDate
                );

        UpdateTransactionRequest request =
                new UpdateTransactionRequest(
                        10L,
                        TransactionType.EXPENSE,
                        new BigDecimal("200000"),
                        "WinMart",
                        "BANK",
                        "Groceries",
                        transactionDate
                );

        TransactionResponse response =
                new TransactionResponse(
                        100L,
                        10L,
                        "Ăn uống",
                        TransactionType.EXPENSE,
                        new BigDecimal("200000"),
                        "WinMart",
                        "BANK",
                        "Groceries",
                        transactionDate,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(
                transactionRepository
                        .findByIdAndUserIdAndDeletedAtIsNull(
                                100L,
                                1L
                        )
        ).thenReturn(
                Optional.of(transaction)
        );

        when(
                categoryRepository.findAccessibleById(
                        10L,
                        1L
                )
        ).thenReturn(
                Optional.of(expenseCategory)
        );

        when(
                transactionRepository.save(
                        transaction
                )
        ).thenReturn(
                transaction
        );

        when(
                transactionMapper.toResponse(
                        transaction
                )
        ).thenReturn(
                response
        );

        TransactionResponse result =
                transactionService.update(
                        100L,
                        request
                );

        assertEquals(
                new BigDecimal("200000"),
                result.amount()
        );

        verify(transactionMapper)
                .updateEntity(
                        request,
                        expenseCategory,
                        transaction
                );

        verify(budgetService)
                .checkBudgetAlert(
                        1L,
                        10L,
                        transactionDate
                );

        verify(dashboardCacheService)
                .evict(
                        1L,
                        transactionDate.getYear(),
                        transactionDate.getMonthValue()
                );
    }

    @Test
    void update_shouldEvictOldAndNewDashboardCache_whenMonthChanges() {
        LocalDate oldDate =
                LocalDate.of(
                        2026,
                        7,
                        31
                );

        LocalDate newDate =
                LocalDate.of(
                        2026,
                        8,
                        1
                );

        Transaction transaction =
                createTransaction(
                        oldDate
                );

        UpdateTransactionRequest request =
                new UpdateTransactionRequest(
                        10L,
                        TransactionType.EXPENSE,
                        new BigDecimal("200000"),
                        "WinMart",
                        "BANK",
                        "Groceries",
                        newDate
                );

        TransactionResponse response =
                new TransactionResponse(
                        100L,
                        10L,
                        "Ăn uống",
                        TransactionType.EXPENSE,
                        new BigDecimal("200000"),
                        "WinMart",
                        "BANK",
                        "Groceries",
                        newDate,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(
                transactionRepository
                        .findByIdAndUserIdAndDeletedAtIsNull(
                                100L,
                                1L
                        )
        ).thenReturn(
                Optional.of(transaction)
        );

        when(
                categoryRepository.findAccessibleById(
                        10L,
                        1L
                )
        ).thenReturn(
                Optional.of(expenseCategory)
        );

        doAnswer(invocation -> {

            Transaction target =
                    invocation.getArgument(2);

            ReflectionTestUtils.setField(
                    target,
                    "transactionDate",
                    newDate
            );

            return null;

        }).when(transactionMapper)
                .updateEntity(
                        request,
                        expenseCategory,
                        transaction
                );

        when(
                transactionRepository.save(
                        transaction
                )
        ).thenReturn(
                transaction
        );

        when(
                transactionMapper.toResponse(
                        transaction
                )
        ).thenReturn(
                response
        );

        transactionService.update(
                100L,
                request
        );

        verify(dashboardCacheService)
                .evict(
                        1L,
                        2026,
                        7
                );

        verify(dashboardCacheService)
                .evict(
                        1L,
                        2026,
                        8
                );

        verify(budgetService)
                .checkBudgetAlert(
                        1L,
                        10L,
                        newDate
                );
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void delete_shouldSoftDeleteTransaction() {
        LocalDate transactionDate =
                LocalDate.now();

        Transaction transaction =
                createTransaction(
                        transactionDate
                );

        when(
                transactionRepository
                        .findByIdAndUserIdAndDeletedAtIsNull(
                                100L,
                                1L
                        )
        ).thenReturn(
                Optional.of(transaction)
        );

        when(
                transactionRepository.save(
                        transaction
                )
        ).thenReturn(
                transaction
        );

        transactionService.delete(
                100L
        );

        assertTrue(
                transaction.isDeleted()
        );

        verify(transactionRepository)
                .save(transaction);

        verify(
                transactionRepository,
                never()
        ).delete(
                any(Transaction.class)
        );

        verify(dashboardCacheService)
                .evict(
                        1L,
                        transactionDate.getYear(),
                        transactionDate.getMonthValue()
                );

        verify(
                budgetService,
                never()
        ).checkBudgetAlert(
                anyLong(),
                anyLong(),
                any(LocalDate.class)
        );
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Test
    void getAll_shouldReturnMappedPage() {
        Transaction transaction =
                createTransaction();

        TransactionResponse response =
                createResponse();

        Page<Transaction> page =
                new PageImpl<>(
                        List.of(
                                transaction
                        )
                );

        when(
                transactionRepository.findAll(
                        any(
                                org.springframework.data.jpa.domain
                                        .Specification.class
                        ),
                        any(
                                org.springframework.data.domain
                                        .Pageable.class
                        )
                )
        ).thenReturn(
                page
        );

        when(
                transactionMapper.toResponse(
                        transaction
                )
        ).thenReturn(
                response
        );

        TransactionFilterRequest filter =
                new TransactionFilterRequest(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        20,
                        "transactionDate",
                        "desc"
                );

        Page<TransactionResponse> result =
                transactionService.getAll(
                        filter
                );

        assertEquals(
                1,
                result.getTotalElements()
        );

        assertEquals(
                100L,
                result
                        .getContent()
                        .getFirst()
                        .id()
        );
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Transaction createTransaction() {
        return createTransaction(
                LocalDate.now()
        );
    }

    private Transaction createTransaction(
            LocalDate transactionDate
    ) {
        Transaction transaction =
                new Transaction(
                        user,
                        expenseCategory,
                        TransactionType.EXPENSE,
                        new BigDecimal("150000"),
                        "Highlands",
                        "CASH",
                        "Cafe",
                        transactionDate
                );

        ReflectionTestUtils.setField(
                transaction,
                "id",
                100L
        );

        return transaction;
    }

    private TransactionResponse createResponse() {
        return new TransactionResponse(
                100L,
                10L,
                "Ăn uống",
                TransactionType.EXPENSE,
                new BigDecimal("150000"),
                "Highlands",
                "CASH",
                "Cafe",
                LocalDate.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}