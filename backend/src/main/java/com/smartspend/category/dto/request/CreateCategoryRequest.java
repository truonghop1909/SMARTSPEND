package com.smartspend.category.dto.request;

import com.smartspend.category.entity.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(
                max = 100,
                message = "Category name must not exceed 100 characters"
        )
        String name,

        @NotNull(message = "Category type is required")
        CategoryType type,

        @Size(
                max = 100,
                message = "Icon must not exceed 100 characters"
        )
        String icon,

        @Size(
                max = 20,
                message = "Color must not exceed 20 characters"
        )
        String color

) {
}