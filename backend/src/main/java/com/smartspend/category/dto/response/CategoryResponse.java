package com.smartspend.category.dto.response;

import com.smartspend.category.entity.CategoryType;

import java.time.LocalDateTime;

public record CategoryResponse(

        Long id,

        String name,

        CategoryType type,

        String icon,

        String color,

        boolean defaultCategory,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}