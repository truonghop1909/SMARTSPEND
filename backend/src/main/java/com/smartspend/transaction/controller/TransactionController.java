package com.smartspend.transaction.controller;

import com.smartspend.transaction.dto.request.CreateTransactionRequest;
import com.smartspend.transaction.dto.request.TransactionFilterRequest;
import com.smartspend.transaction.dto.request.UpdateTransactionRequest;
import com.smartspend.transaction.dto.response.TransactionResponse;
import com.smartspend.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService
    ) {
        this.transactionService = transactionService;
    }

    // =========================
    // GET ALL
    // =========================

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getAll(
            @Valid
            @ModelAttribute
            TransactionFilterRequest filter
    ) {
        return ResponseEntity.ok(
                transactionService.getAll(filter)
        );
    }

    // =========================
    // GET BY ID
    // =========================

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getById(
            @PathVariable Long transactionId
    ) {
        return ResponseEntity.ok(
                transactionService.getById(
                        transactionId
                )
        );
    }

    // =========================
    // CREATE
    // =========================

    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @Valid
            @RequestBody
            CreateTransactionRequest request
    ) {
        TransactionResponse response =
                transactionService.create(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================
    // UPDATE
    // =========================

    @PatchMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> update(
            @PathVariable Long transactionId,

            @Valid
            @RequestBody
            UpdateTransactionRequest request
    ) {
        return ResponseEntity.ok(
                transactionService.update(
                        transactionId,
                        request
                )
        );
    }

    // =========================
    // DELETE
    // =========================

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long transactionId
    ) {
        transactionService.delete(
                transactionId
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}