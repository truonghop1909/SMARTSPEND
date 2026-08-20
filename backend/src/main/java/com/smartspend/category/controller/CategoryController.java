package com.smartspend.category.controller;

import com.smartspend.category.dto.request.CreateCategoryRequest;
import com.smartspend.category.dto.request.UpdateCategoryRequest;
import com.smartspend.category.dto.response.CategoryResponse;
import com.smartspend.category.entity.CategoryType;
import com.smartspend.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(
            CategoryService categoryService
    ) {
        this.categoryService =
                categoryService;
    }

    // =========================
    // GET ALL
    // =========================

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll(
            @RequestParam(
                    required = false
            )
            CategoryType type
    ) {
        return ResponseEntity.ok(
                categoryService.getAll(type)
        );
    }

    // =========================
    // GET BY ID
    // =========================

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getById(
            @PathVariable Long categoryId
    ) {
        return ResponseEntity.ok(
                categoryService.getById(
                        categoryId
                )
        );
    }

    // =========================
    // CREATE
    // =========================

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @Valid
            @RequestBody
            CreateCategoryRequest request
    ) {
        CategoryResponse response =
                categoryService.create(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================
    // UPDATE
    // =========================

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long categoryId,

            @Valid
            @RequestBody
            UpdateCategoryRequest request
    ) {
        return ResponseEntity.ok(
                categoryService.update(
                        categoryId,
                        request
                )
        );
    }

    // =========================
    // DELETE
    // =========================

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long categoryId
    ) {
        categoryService.delete(
                categoryId
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}