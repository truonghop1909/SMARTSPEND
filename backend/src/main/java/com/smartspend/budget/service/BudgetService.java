package com.smartspend.budget.service;

import com.smartspend.budget.dto.request.CreateBudgetRequest;
import com.smartspend.budget.dto.request.UpdateBudgetRequest;
import com.smartspend.budget.dto.response.BudgetResponse;

import java.time.LocalDate;
import java.util.List;

public interface BudgetService {

    List<BudgetResponse> getAll(
            Integer year,
            Integer month
    );

    BudgetResponse getById(
            Long budgetId
    );

    BudgetResponse create(
            CreateBudgetRequest request
    );

    BudgetResponse update(
            Long budgetId,
            UpdateBudgetRequest request
    );

    void delete(
            Long budgetId
    );

    void checkBudgetAlert(
            Long userId,
            Long categoryId,
            LocalDate transactionDate
    );
}