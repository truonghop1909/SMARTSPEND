package com.smartspend.transaction.service;

import com.smartspend.transaction.dto.request.CreateTransactionRequest;
import com.smartspend.transaction.dto.request.TransactionFilterRequest;
import com.smartspend.transaction.dto.request.UpdateTransactionRequest;
import com.smartspend.transaction.dto.response.TransactionResponse;
import org.springframework.data.domain.Page;

public interface TransactionService {

    Page<TransactionResponse> getAll(
            TransactionFilterRequest filter
    );

    TransactionResponse getById(
            Long transactionId
    );

    TransactionResponse create(
            CreateTransactionRequest request
    );

    TransactionResponse update(
            Long transactionId,
            UpdateTransactionRequest request
    );

    void delete(
            Long transactionId
    );
}