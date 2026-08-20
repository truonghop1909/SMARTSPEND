package com.smartspend.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartspend.category.dto.request.CreateCategoryRequest;
import com.smartspend.category.dto.request.UpdateCategoryRequest;
import com.smartspend.category.dto.response.CategoryResponse;
import com.smartspend.category.entity.CategoryType;
import com.smartspend.category.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void getAll_shouldReturnUnauthorized_withoutAuthentication()
            throws Exception {

        mockMvc.perform(
                        get("/api/categories")
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    @WithMockUser
    void getAll_shouldReturnCategories()
            throws Exception {

        CategoryResponse category =
                createResponse();

        when(
                categoryService.getAll(null)
        ).thenReturn(
                List.of(category)
        );

        mockMvc.perform(
                        get("/api/categories")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].name")
                                .value("Ăn uống")
                )
                .andExpect(
                        jsonPath("$[0].type")
                                .value("EXPENSE")
                );
    }

    @Test
    @WithMockUser
    void getAll_shouldFilterByType()
            throws Exception {

        when(
                categoryService.getAll(
                        CategoryType.EXPENSE
                )
        ).thenReturn(
                List.of(createResponse())
        );

        mockMvc.perform(
                        get("/api/categories")
                                .param(
                                        "type",
                                        "EXPENSE"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].type")
                                .value("EXPENSE")
                );
    }

    @Test
    @WithMockUser
    void getById_shouldReturnCategory()
            throws Exception {

        when(
                categoryService.getById(1L)
        ).thenReturn(
                createResponse()
        );

        mockMvc.perform(
                        get("/api/categories/1")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Ăn uống")
                );
    }

    @Test
    @WithMockUser
    void create_shouldReturnCreated()
            throws Exception {

        CreateCategoryRequest request =
                new CreateCategoryRequest(
                        "Cafe",
                        CategoryType.EXPENSE,
                        "coffee",
                        "#FFFFFF"
                );

        CategoryResponse response =
                new CategoryResponse(
                        10L,
                        "Cafe",
                        CategoryType.EXPENSE,
                        "coffee",
                        "#FFFFFF",
                        false,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(
                categoryService.create(
                        any(CreateCategoryRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/categories")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request
                                                )
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Cafe")
                )
                .andExpect(
                        jsonPath("$.defaultCategory")
                                .value(false)
                );
    }

    @Test
    @WithMockUser
    void create_shouldReturnBadRequest_whenNameBlank()
            throws Exception {

        String json = """
                {
                    "name": "",
                    "type": "EXPENSE"
                }
                """;

        mockMvc.perform(
                        post("/api/categories")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(json)
                )
                .andExpect(
                        status().isBadRequest()
                );

        verify(
                categoryService,
                never()
        ).create(any());
    }

    @Test
    @WithMockUser
    void update_shouldReturnUpdatedCategory()
            throws Exception {

        UpdateCategoryRequest request =
                new UpdateCategoryRequest(
                        "Ăn sáng",
                        CategoryType.EXPENSE,
                        null,
                        null
                );

        CategoryResponse response =
                new CategoryResponse(
                        10L,
                        "Ăn sáng",
                        CategoryType.EXPENSE,
                        null,
                        null,
                        false,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(
                categoryService.update(
                        eq(10L),
                        any(UpdateCategoryRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        put("/api/categories/10")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request
                                                )
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Ăn sáng")
                );
    }

    @Test
    @WithMockUser
    void delete_shouldReturnNoContent()
            throws Exception {

        doNothing()
                .when(categoryService)
                .delete(10L);

        mockMvc.perform(
                        delete("/api/categories/10")
                )
                .andExpect(
                        status().isNoContent()
                );

        verify(
                categoryService
        ).delete(10L);
    }

    private CategoryResponse createResponse() {
        return new CategoryResponse(
                1L,
                "Ăn uống",
                CategoryType.EXPENSE,
                "food",
                "#FFFFFF",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}