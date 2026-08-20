package com.smartspend.category.service;

import com.smartspend.category.dto.request.CreateCategoryRequest;
import com.smartspend.category.dto.request.UpdateCategoryRequest;
import com.smartspend.category.dto.response.CategoryResponse;
import com.smartspend.category.entity.CategoryType;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getAll(
            CategoryType type
    );

    CategoryResponse getById(
            Long categoryId
    );

    CategoryResponse create(
            CreateCategoryRequest request
    );

    CategoryResponse update(
            Long categoryId,
            UpdateCategoryRequest request
    );

    void delete(
            Long categoryId
    );
}