package com.smartspend.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartspend.transaction.dto.request.CreateTransactionRequest;
import com.smartspend.transaction.dto.request.UpdateTransactionRequest;
import com.smartspend.transaction.dto.response.TransactionResponse;
import com.smartspend.transaction.entity.TransactionType;
import com.smartspend.transaction.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.test.context.support.WithAnonymousUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    void getAll_shouldReturnUnauthorized_withoutAuthentication()
            throws Exception {

        mockMvc.perform(
                get("/api/transactions")
                        .with(anonymous()))
                .andExpect(
                        status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getAll_shouldReturnTransactions()
            throws Exception {

        when(
                transactionService.getAll(any())).thenReturn(
                        new PageImpl<>(
                                List.of(
                                        createResponse())));

        mockMvc.perform(
                get("/api/transactions"))
                .andExpect(
                        status().isOk())
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value(100))
                .andExpect(
                        jsonPath("$.content[0].type")
                                .value("EXPENSE"));
    }

    @Test
    @WithMockUser
    void getById_shouldReturnTransaction()
            throws Exception {

        when(
                transactionService.getById(100L)).thenReturn(
                        createResponse());

        mockMvc.perform(
                get("/api/transactions/100"))
                .andExpect(
                        status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(100));
    }

    @Test
    @WithMockUser
    void create_shouldReturnCreated()
            throws Exception {

        CreateTransactionRequest request = new CreateTransactionRequest(
                10L,
                TransactionType.EXPENSE,
                new BigDecimal("150000"),
                "Highlands",
                "CASH",
                "Cafe",
                LocalDate.now());

        when(
                transactionService.create(
                        any(CreateTransactionRequest.class)))
                .thenReturn(
                        createResponse());

        mockMvc.perform(
                post("/api/transactions")
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        request)))
                .andExpect(
                        status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value(100))
                .andExpect(
                        jsonPath("$.amount")
                                .value(150000));
    }

    @Test
    @WithMockUser
    void create_shouldReturnBadRequest_whenAmountInvalid()
            throws Exception {

        String json = """
                {
                  "categoryId": 10,
                  "type": "EXPENSE",
                  "amount": 0,
                  "transactionDate": "2026-08-12"
                }
                """;

        mockMvc.perform(
                post("/api/transactions")
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(
                        status().isBadRequest());

        verify(
                transactionService,
                never()).create(any());
    }

    @Test
    @WithMockUser
    void update_shouldReturnUpdatedTransaction()
            throws Exception {

        UpdateTransactionRequest request = new UpdateTransactionRequest(
                10L,
                TransactionType.EXPENSE,
                new BigDecimal("200000"),
                "WinMart",
                "BANK",
                "Groceries",
                LocalDate.now());

        TransactionResponse response = new TransactionResponse(
                100L,
                10L,
                "Ăn uống",
                TransactionType.EXPENSE,
                new BigDecimal("200000"),
                "WinMart",
                "BANK",
                "Groceries",
                LocalDate.now(),
                LocalDateTime.now(),
                LocalDateTime.now());

        when(
                transactionService.update(
                        eq(100L),
                        any(UpdateTransactionRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                patch("/api/transactions/100")
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        request)))
                .andExpect(
                        status().isOk())
                .andExpect(
                        jsonPath("$.amount")
                                .value(200000));
    }

    @Test
    @WithMockUser
    void delete_shouldReturnNoContent()
            throws Exception {

        doNothing()
                .when(transactionService)
                .delete(100L);

        mockMvc.perform(
                delete("/api/transactions/100"))
                .andExpect(
                        status().isNoContent());

        verify(
                transactionService).delete(100L);
    }

    private TransactionResponse createResponse() {
        return new TransactionResponse(
                100L,
                10L,
                "Ăn uống",
                TransactionType.EXPENSE,
                new BigDecimal("150000"),
                "Highlands",
                "CASH",
                "Cafe",
                LocalDate.now(),
                LocalDateTime.now(),
                LocalDateTime.now());
    }
}