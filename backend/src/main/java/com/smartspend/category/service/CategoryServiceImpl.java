package com.smartspend.category.service;

import com.smartspend.auth.entity.User;
import com.smartspend.auth.repository.UserRepository;
import com.smartspend.category.dto.request.CreateCategoryRequest;
import com.smartspend.category.dto.request.UpdateCategoryRequest;
import com.smartspend.category.dto.response.CategoryResponse;
import com.smartspend.category.entity.Category;
import com.smartspend.category.entity.CategoryType;
import com.smartspend.category.mapper.CategoryMapper;
import com.smartspend.category.repository.CategoryRepository;
import com.smartspend.common.exception.AppException;
import com.smartspend.common.exception.ErrorCode;
import com.smartspend.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl
        implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            CategoryMapper categoryMapper
    ) {
        this.categoryRepository =
                categoryRepository;

        this.userRepository =
                userRepository;

        this.categoryMapper =
                categoryMapper;
    }

    // =========================
    // GET ALL
    // =========================

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll(
            CategoryType type
    ) {
        Long userId =
                getCurrentUserId();

        List<Category> categories;

        if (type == null) {
            categories =
                    categoryRepository
                            .findAvailableCategories(
                                    userId
                            );
        } else {
            categories =
                    categoryRepository
                            .findAvailableCategoriesByType(
                                    userId,
                                    type
                            );
        }

        return categories
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    // =========================
    // GET BY ID
    // =========================

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(
            Long categoryId
    ) {
        Long userId =
                getCurrentUserId();

        Category category =
                categoryRepository
                        .findAccessibleById(
                                categoryId,
                                userId
                        )
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.CATEGORY_NOT_FOUND
                                )
                        );

        return categoryMapper.toResponse(
                category
        );
    }

    // =========================
    // CREATE
    // =========================

    @Override
    @Transactional
    public CategoryResponse create(
            CreateCategoryRequest request
    ) {
        Long userId =
                getCurrentUserId();

        String normalizedName =
                normalizeName(request.name());

        boolean exists =
                categoryRepository
                        .existsByUserIdAndNameIgnoreCaseAndType(
                                userId,
                                normalizedName,
                                request.type()
                        );

        if (exists) {
            throw new AppException(
                    ErrorCode.CATEGORY_ALREADY_EXISTS
            );
        }

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.USER_NOT_FOUND
                                )
                        );

        Category category =
                categoryMapper.toEntity(
                        request,
                        user
                );

        Category savedCategory =
                categoryRepository.save(
                        category
                );

        return categoryMapper.toResponse(
                savedCategory
        );
    }

    // =========================
    // UPDATE
    // =========================

    @Override
    @Transactional
    public CategoryResponse update(
            Long categoryId,
            UpdateCategoryRequest request
    ) {
        Long userId =
                getCurrentUserId();

        Category category =
                categoryRepository
                        .findAccessibleById(
                                categoryId,
                                userId
                        )
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.CATEGORY_NOT_FOUND
                                )
                        );

        if (category.isSystemDefault()) {
            throw new AppException(
                    ErrorCode.CATEGORY_DEFAULT_IMMUTABLE
            );
        }

        if (!category.isOwnedBy(userId)) {
            throw new AppException(
                    ErrorCode.CATEGORY_NOT_FOUND
            );
        }

        String normalizedName =
                normalizeName(request.name());

        boolean duplicate =
                categoryRepository
                        .existsDuplicateForUpdate(
                                userId,
                                normalizedName,
                                request.type(),
                                categoryId
                        );

        if (duplicate) {
            throw new AppException(
                    ErrorCode.CATEGORY_ALREADY_EXISTS
            );
        }

        categoryMapper.updateEntity(
                request,
                category
        );

        Category updatedCategory =
                categoryRepository.save(
                        category
                );

        return categoryMapper.toResponse(
                updatedCategory
        );
    }

    // =========================
    // DELETE
    // =========================

    @Override
    @Transactional
    public void delete(
            Long categoryId
    ) {
        Long userId =
                getCurrentUserId();

        Category category =
                categoryRepository
                        .findAccessibleById(
                                categoryId,
                                userId
                        )
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.CATEGORY_NOT_FOUND
                                )
                        );

        if (category.isSystemDefault()) {
            throw new AppException(
                    ErrorCode.CATEGORY_DEFAULT_IMMUTABLE
            );
        }

        if (!category.isOwnedBy(userId)) {
            throw new AppException(
                    ErrorCode.CATEGORY_NOT_FOUND
            );
        }

        long transactionCount =
                categoryRepository
                        .countActiveTransactionsByCategoryId(
                                categoryId
                        );

        if (transactionCount > 0) {
            throw new AppException(
                    ErrorCode.CATEGORY_IN_USE
            );
        }

        categoryRepository.delete(
                category
        );
    }

    // =========================
    // CURRENT USER
    // =========================

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

    // =========================
    // NORMALIZE
    // =========================

    private String normalizeName(
            String name
    ) {
        return name
                .trim()
                .replaceAll("\\s+", " ");
    }
}