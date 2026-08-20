package com.smartspend.budget.controller;

import com.smartspend.budget.dto.request.CreateBudgetRequest;
import com.smartspend.budget.dto.request.UpdateBudgetRequest;
import com.smartspend.budget.dto.response.BudgetResponse;
import com.smartspend.budget.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(
            BudgetService budgetService
    ) {
        this.budgetService =
                budgetService;
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAll(
            @RequestParam(required = false)
            Integer year,

            @RequestParam(required = false)
            Integer month
    ) {
        return ResponseEntity.ok(
                budgetService.getAll(
                        year,
                        month
                )
        );
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> getById(
            @PathVariable Long budgetId
    ) {
        return ResponseEntity.ok(
                budgetService.getById(
                        budgetId
                )
        );
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> create(
            @Valid
            @RequestBody
            CreateBudgetRequest request
    ) {
        BudgetResponse response =
                budgetService.create(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> update(
            @PathVariable Long budgetId,

            @Valid
            @RequestBody
            UpdateBudgetRequest request
    ) {
        return ResponseEntity.ok(
                budgetService.update(
                        budgetId,
                        request
                )
        );
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long budgetId
    ) {
        budgetService.delete(
                budgetId
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}