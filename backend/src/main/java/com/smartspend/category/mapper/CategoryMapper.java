package com.smartspend.category.mapper;

import com.smartspend.auth.entity.User;
import com.smartspend.category.dto.request.CreateCategoryRequest;
import com.smartspend.category.dto.request.UpdateCategoryRequest;
import com.smartspend.category.dto.response.CategoryResponse;
import com.smartspend.category.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    default Category toEntity(
            CreateCategoryRequest request,
            User user
    ) {
        Objects.requireNonNull(
                request,
                "Create category request must not be null"
        );

        Objects.requireNonNull(
                user,
                "User must not be null"
        );

        return new Category(
                user,
                normalizeName(request.name()),
                request.type(),
                normalizeNullable(request.icon()),
                normalizeNullable(request.color()),
                false
        );
    }

    default void updateEntity(
            UpdateCategoryRequest request,
            @MappingTarget Category category
    ) {
        Objects.requireNonNull(
                request,
                "Update category request must not be null"
        );

        Objects.requireNonNull(
                category,
                "Category must not be null"
        );

        category.setName(
                normalizeName(request.name())
        );

        category.setType(
                request.type()
        );

        category.setIcon(
                normalizeNullable(request.icon())
        );

        category.setColor(
                normalizeNullable(request.color())
        );
    }

    private String normalizeName(String name) {
        return name
                .trim()
                .replaceAll("\\s+", " ");
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        return normalized.isBlank()
                ? null
                : normalized;
    }
}