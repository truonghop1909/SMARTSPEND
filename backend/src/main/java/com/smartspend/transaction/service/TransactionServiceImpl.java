package com.smartspend.transaction.service;

import com.smartspend.auth.entity.User;
import com.smartspend.auth.repository.UserRepository;
import com.smartspend.budget.service.BudgetService;
import com.smartspend.category.entity.Category;
import com.smartspend.category.repository.CategoryRepository;
import com.smartspend.common.exception.AppException;
import com.smartspend.common.exception.ErrorCode;
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
import com.smartspend.transaction.repository.TransactionSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Locale;
import java.util.Set;

@Service
public class TransactionServiceImpl
        implements TransactionService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    TransactionServiceImpl.class
            );

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "transactionDate",
                    "amount",
                    "createdAt",
                    "updatedAt"
            );

    private static final String DEFAULT_SORT_FIELD =
            "transactionDate";

    private static final String FINANCIAL_CONTEXT_CACHE_PREFIX =
            "financial-context:user:";

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final BudgetService budgetService;
    private final DashboardCacheService dashboardCacheService;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            TransactionMapper transactionMapper,
            RedisTemplate<String, Object> redisTemplate,
            BudgetService budgetService,
            DashboardCacheService dashboardCacheService
    ) {
        this.transactionRepository =
                transactionRepository;

        this.categoryRepository =
                categoryRepository;

        this.userRepository =
                userRepository;

        this.transactionMapper =
                transactionMapper;

        this.redisTemplate =
                redisTemplate;

        this.budgetService =
                budgetService;

        this.dashboardCacheService =
                dashboardCacheService;
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAll(
            TransactionFilterRequest filter
    ) {
        Long userId =
                getCurrentUserId();

        validateFilter(filter);

        Pageable pageable =
                createPageable(filter);

        Specification<Transaction> specification =
                TransactionSpecifications.filter(
                        userId,
                        filter.type(),
                        filter.categoryId(),
                        filter.fromDate(),
                        filter.toDate(),
                        filter.keyword()
                );

        specification =
                specification
                        .and(
                                amountGreaterThanOrEqual(
                                        filter.minAmount()
                                )
                        )
                        .and(
                                amountLessThanOrEqual(
                                        filter.maxAmount()
                                )
                        );

        return transactionRepository
                .findAll(
                        specification,
                        pageable
                )
                .map(
                        transactionMapper::toResponse
                );
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getById(
            Long transactionId
    ) {
        Long userId =
                getCurrentUserId();

        Transaction transaction =
                findOwnedTransaction(
                        transactionId,
                        userId
                );

        return transactionMapper.toResponse(
                transaction
        );
    }

    // =========================================================
    // CREATE
    // =========================================================

    @Override
    @Transactional
    public TransactionResponse create(
            CreateTransactionRequest request
    ) {
        Long userId =
                getCurrentUserId();

        validateAmount(
                request.amount()
        );

        validateTransactionDate(
                request.transactionDate()
        );

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.USER_NOT_FOUND
                                )
                        );

        Category category =
                findAccessibleCategory(
                        request.categoryId(),
                        userId
                );

        validateCategoryType(
                category,
                request.type()
        );

        Transaction transaction =
                transactionMapper.toEntity(
                        request,
                        user,
                        category
                );

        Transaction savedTransaction =
                transactionRepository.save(
                        transaction
                );

        budgetService.checkBudgetAlert(
                userId,
                savedTransaction.getCategory().getId(),
                savedTransaction.getTransactionDate()
        );

        evictDashboardCache(
                userId,
                savedTransaction.getTransactionDate()
        );

        invalidateFinancialContextCache(
                userId
        );

        return transactionMapper.toResponse(
                savedTransaction
        );
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Override
    @Transactional
    public TransactionResponse update(
            Long transactionId,
            UpdateTransactionRequest request
    ) {
        Long userId =
                getCurrentUserId();

        validateAmount(
                request.amount()
        );

        validateTransactionDate(
                request.transactionDate()
        );

        Transaction transaction =
                findOwnedTransaction(
                        transactionId,
                        userId
                );

        /*
         * Phải lưu lại ngày cũ trước khi mapper update,
         * vì transactionDate có thể đổi sang tháng khác.
         */
        LocalDate oldTransactionDate =
                transaction.getTransactionDate();

        Category category =
                findAccessibleCategory(
                        request.categoryId(),
                        userId
                );

        validateCategoryType(
                category,
                request.type()
        );

        transactionMapper.updateEntity(
                request,
                category,
                transaction
        );

        Transaction updatedTransaction =
                transactionRepository.save(
                        transaction
                );

        LocalDate newTransactionDate =
                updatedTransaction
                        .getTransactionDate();

        budgetService.checkBudgetAlert(
                userId,
                updatedTransaction
                        .getCategory()
                        .getId(),
                newTransactionDate
        );

        /*
         * Nếu Transaction đổi từ tháng cũ sang tháng mới,
         * cả hai Dashboard đều đã stale.
         */
        evictDashboardCache(
                userId,
                oldTransactionDate
        );

        if (!isSameMonth(
                oldTransactionDate,
                newTransactionDate
        )) {
            evictDashboardCache(
                    userId,
                    newTransactionDate
            );
        }

        invalidateFinancialContextCache(
                userId
        );

        return transactionMapper.toResponse(
                updatedTransaction
        );
    }

    // =========================================================
    // SOFT DELETE
    // =========================================================

    @Override
    @Transactional
    public void delete(
            Long transactionId
    ) {
        Long userId =
                getCurrentUserId();

        Transaction transaction =
                findOwnedTransaction(
                        transactionId,
                        userId
                );

        LocalDate transactionDate =
                transaction.getTransactionDate();

        transaction.softDelete();

        transactionRepository.save(
                transaction
        );

        /*
         * Soft Delete làm tổng Income/Expense của tháng thay đổi,
         * vì vậy Dashboard tháng đó phải bị xóa cache.
         */
        evictDashboardCache(
                userId,
                transactionDate
        );

        invalidateFinancialContextCache(
                userId
        );
    }

    // =========================================================
    // TRANSACTION OWNERSHIP
    // =========================================================

    private Transaction findOwnedTransaction(
            Long transactionId,
            Long userId
    ) {
        return transactionRepository
                .findByIdAndUserIdAndDeletedAtIsNull(
                        transactionId,
                        userId
                )
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.TRANSACTION_NOT_FOUND
                        )
                );
    }

    // =========================================================
    // CATEGORY
    // =========================================================

    private Category findAccessibleCategory(
            Long categoryId,
            Long userId
    ) {
        return categoryRepository
                .findAccessibleById(
                        categoryId,
                        userId
                )
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.CATEGORY_NOT_FOUND
                        )
                );
    }

    private void validateCategoryType(
            Category category,
            TransactionType transactionType
    ) {
        if (!category
                .getType()
                .name()
                .equals(
                        transactionType.name()
                )) {

            throw new AppException(
                    ErrorCode.TRANSACTION_CATEGORY_TYPE_MISMATCH
            );
        }
    }

    // =========================================================
    // AMOUNT
    // =========================================================

    private void validateAmount(
            BigDecimal amount
    ) {
        if (amount == null
                || amount.compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

            throw new AppException(
                    ErrorCode.TRANSACTION_INVALID_AMOUNT
            );
        }
    }

    // =========================================================
    // DATE
    // =========================================================

    private void validateTransactionDate(
            LocalDate transactionDate
    ) {
        if (transactionDate == null) {
            throw new AppException(
                    ErrorCode.INVALID_REQUEST
            );
        }

        if (transactionDate.isAfter(
                LocalDate.now()
        )) {
            throw new AppException(
                    ErrorCode.TRANSACTION_FUTURE_DATE
            );
        }
    }

    // =========================================================
    // FILTER VALIDATION
    // =========================================================

    private void validateFilter(
            TransactionFilterRequest filter
    ) {
        if (filter.fromDate() != null
                && filter.toDate() != null
                && filter.fromDate()
                .isAfter(
                        filter.toDate()
                )) {

            throw new AppException(
                    ErrorCode.TRANSACTION_INVALID_DATE_RANGE
            );
        }

        if (filter.minAmount() != null
                && filter.maxAmount() != null
                && filter.minAmount()
                .compareTo(
                        filter.maxAmount()
                ) > 0) {

            throw new AppException(
                    ErrorCode.TRANSACTION_INVALID_AMOUNT_RANGE
            );
        }
    }

    // =========================================================
    // AMOUNT SPECIFICATION
    // =========================================================

    private Specification<Transaction>
    amountGreaterThanOrEqual(
            BigDecimal minAmount
    ) {
        if (minAmount == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder
                        .greaterThanOrEqualTo(
                                root.get("amount"),
                                minAmount
                        );
    }

    private Specification<Transaction>
    amountLessThanOrEqual(
            BigDecimal maxAmount
    ) {
        if (maxAmount == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder
                        .lessThanOrEqualTo(
                                root.get("amount"),
                                maxAmount
                        );
    }

    // =========================================================
    // PAGINATION + SORT
    // =========================================================

    private Pageable createPageable(
            TransactionFilterRequest filter
    ) {
        String sortField =
                resolveSortField(
                        filter.resolvedSortBy()
                );

        Sort.Direction direction =
                resolveSortDirection(
                        filter.resolvedSortDirection()
                );

        Sort sort =
                Sort.by(
                        direction,
                        sortField
                );

        return PageRequest.of(
                filter.resolvedPage(),
                filter.resolvedSize(),
                sort
        );
    }

    private String resolveSortField(
            String requestedField
    ) {
        if (requestedField == null
                || !ALLOWED_SORT_FIELDS
                .contains(requestedField)) {

            return DEFAULT_SORT_FIELD;
        }

        return requestedField;
    }

    private Sort.Direction resolveSortDirection(
            String direction
    ) {
        if (direction == null) {
            return Sort.Direction.DESC;
        }

        return "asc".equals(
                direction
                        .trim()
                        .toLowerCase(
                                Locale.ROOT
                        )
        )
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
    }

    // =========================================================
    // CURRENT USER
    // =========================================================

    private Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal()
                instanceof UserPrincipal principal)) {

            throw new AppException(
                    ErrorCode.UNAUTHORIZED
            );
        }

        return principal.getId();
    }

    // =========================================================
    // DASHBOARD CACHE
    // =========================================================

    private void evictDashboardCache(
            Long userId,
            LocalDate transactionDate
    ) {
        dashboardCacheService.evict(
                userId,
                transactionDate.getYear(),
                transactionDate.getMonthValue()
        );
    }

    private boolean isSameMonth(
            LocalDate firstDate,
            LocalDate secondDate
    ) {
        return YearMonth.from(firstDate)
                .equals(
                        YearMonth.from(secondDate)
                );
    }

    // =========================================================
    // FINANCIAL CONTEXT CACHE
    // =========================================================

    private void invalidateFinancialContextCache(
            Long userId
    ) {
        try {
            redisTemplate.delete(
                    FINANCIAL_CONTEXT_CACHE_PREFIX
                            + userId
            );

        } catch (DataAccessException exception) {
            /*
             * Financial Context cache là lớp hỗ trợ.
             * Redis lỗi không được rollback Transaction trong MySQL.
             */
            log.warn(
                    "Unable to invalidate financial context cache. userId={}",
                    userId
            );
        }
    }
}