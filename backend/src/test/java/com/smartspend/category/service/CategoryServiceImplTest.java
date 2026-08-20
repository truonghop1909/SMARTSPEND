package com.smartspend.category.service;

import com.smartspend.auth.entity.User;
import com.smartspend.auth.enums.AuthProvider;
import com.smartspend.category.dto.request.CreateCategoryRequest;
import com.smartspend.category.dto.request.UpdateCategoryRequest;
import com.smartspend.category.dto.response.CategoryResponse;
import com.smartspend.category.entity.Category;
import com.smartspend.category.entity.CategoryType;
import com.smartspend.category.mapper.CategoryMapper;
import com.smartspend.category.repository.CategoryRepository;
import com.smartspend.auth.repository.UserRepository;
import com.smartspend.common.exception.AppException;
import com.smartspend.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryMapper categoryMapper;

    private CategoryServiceImpl categoryService;

    private User user;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(
                categoryRepository,
                userRepository,
                categoryMapper);

        user = new User(
                "user@example.com",
                "password-hash",
                "Nguyen Van A",
                AuthProvider.LOCAL);

        ReflectionTestUtils.setField(
                user,
                "id",
                1L);

        UserPrincipal principal = UserPrincipal.from(user);

        UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(
                principal,
                null,
                principal.getAuthorities());

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }

    @Test
    void getAll_shouldReturnAvailableCategories() {
        Category category = new Category(
                null,
                "Ăn uống",
                CategoryType.EXPENSE,
                null,
                null,
                true);

        CategoryResponse response = new CategoryResponse(
                1L,
                "Ăn uống",
                CategoryType.EXPENSE,
                null,
                null,
                true,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(categoryRepository.findAvailableCategories(any()))
                .thenReturn(List.of(category));

        when(categoryMapper.toResponse(category))
                .thenReturn(response);

        List<CategoryResponse> result = categoryService.getAll(null);

        assertEquals(1, result.size());
        assertEquals("Ăn uống", result.getFirst().name());
    }

    @Test
    void getAll_shouldFilterByType() {
        Category category = new Category(
                null,
                "Lương",
                CategoryType.INCOME,
                null,
                null,
                true);

        CategoryResponse response = new CategoryResponse(
                1L,
                "Lương",
                CategoryType.INCOME,
                null,
                null,
                true,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(
                categoryRepository.findAvailableCategoriesByType(
                        any(),
                        eq(CategoryType.INCOME)))
                .thenReturn(List.of(category));

        when(categoryMapper.toResponse(category))
                .thenReturn(response);

        List<CategoryResponse> result = categoryService.getAll(CategoryType.INCOME);

        assertEquals(1, result.size());
        assertEquals(CategoryType.INCOME, result.getFirst().type());
    }

    @Test
    void getById_shouldThrowException_whenCategoryNotFound() {
        when(
                categoryRepository.findAccessibleById(
                        anyLong(),
                        any()))
                .thenReturn(Optional.empty());

        assertThrows(
                AppException.class,
                () -> categoryService.getById(999L));
    }

    @Test
    void create_shouldSavePersonalCategory() {
        CreateCategoryRequest request = new CreateCategoryRequest(
                "Ăn sáng",
                CategoryType.EXPENSE,
                "food",
                "#FFFFFF");

        Category category = new Category(
                user,
                "Ăn sáng",
                CategoryType.EXPENSE,
                "food",
                "#FFFFFF",
                false);

        CategoryResponse expectedResponse = new CategoryResponse(
                10L,
                "Ăn sáng",
                CategoryType.EXPENSE,
                "food",
                "#FFFFFF",
                false,
                LocalDateTime.now(),
                LocalDateTime.now());

        when(
                categoryRepository
                        .existsByUserIdAndNameIgnoreCaseAndType(
                                any(),
                                eq("Ăn sáng"),
                                eq(CategoryType.EXPENSE)))
                .thenReturn(false);

        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        when(categoryMapper.toEntity(request, user))
                .thenReturn(category);

        when(categoryRepository.save(category))
                .thenReturn(category);

        when(categoryMapper.toResponse(category))
                .thenReturn(expectedResponse);

        CategoryResponse result = categoryService.create(request);

        assertNotNull(result);
        assertEquals("Ăn sáng", result.name());

        verify(categoryRepository).save(category);
    }

    @Test
    void create_shouldThrowException_whenDuplicateExists() {
        CreateCategoryRequest request = new CreateCategoryRequest(
                "Ăn uống",
                CategoryType.EXPENSE,
                null,
                null);

        when(
                categoryRepository
                        .existsByUserIdAndNameIgnoreCaseAndType(
                                any(),
                                eq("Ăn uống"),
                                eq(CategoryType.EXPENSE)))
                .thenReturn(true);

        assertThrows(
                AppException.class,
                () -> categoryService.create(request));

        verify(categoryRepository, never())
                .save(any());
    }

    @Test
    void update_shouldRejectDefaultCategory() {
        Category defaultCategory = new Category(
                null,
                "Ăn uống",
                CategoryType.EXPENSE,
                null,
                null,
                true);

        when(
                categoryRepository.findAccessibleById(
                        eq(1L),
                        any()))
                .thenReturn(Optional.of(defaultCategory));

        UpdateCategoryRequest request = new UpdateCategoryRequest(
                "Food",
                CategoryType.EXPENSE,
                null,
                null);

        assertThrows(
                AppException.class,
                () -> categoryService.update(
                        1L,
                        request));

        verify(categoryRepository, never())
                .save(any());
    }

    @Test
    void delete_shouldRejectDefaultCategory() {
        Category defaultCategory = new Category(
                null,
                "Ăn uống",
                CategoryType.EXPENSE,
                null,
                null,
                true);

        when(
                categoryRepository.findAccessibleById(
                        eq(1L),
                        any()))
                .thenReturn(Optional.of(defaultCategory));

        assertThrows(
                AppException.class,
                () -> categoryService.delete(1L));

        verify(categoryRepository, never())
                .delete(any());
    }

    @Test
    void delete_shouldRejectCategory_whenTransactionExists() {
        Category category = new Category(
                user,
                "Ăn sáng",
                CategoryType.EXPENSE,
                null,
                null,
                false);

        when(
                categoryRepository.findAccessibleById(
                        eq(10L),
                        any()))
                .thenReturn(Optional.of(category));

        when(
                categoryRepository
                        .countActiveTransactionsByCategoryId(10L))
                .thenReturn(2L);

        assertThrows(
                AppException.class,
                () -> categoryService.delete(10L));

        verify(categoryRepository, never())
                .delete(any());
    }

    @Test
    void delete_shouldDeleteCategory_whenUnused() {
        Category category = new Category(
                user,
                "Ăn sáng",
                CategoryType.EXPENSE,
                null,
                null,
                false);

        when(
                categoryRepository.findAccessibleById(
                        eq(10L),
                        any()))
                .thenReturn(Optional.of(category));

        when(
                categoryRepository
                        .countActiveTransactionsByCategoryId(10L))
                .thenReturn(0L);

        categoryService.delete(10L);

        verify(categoryRepository)
                .delete(category);
    }
}