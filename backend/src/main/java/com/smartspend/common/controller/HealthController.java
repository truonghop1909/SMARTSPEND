package com.smartspend.common.controller;

import com.smartspend.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(
        name = "Health Check",
        description = "Kiểm tra trạng thái hoạt động của SmartSpend API"
)
public class HealthController {

    private final String applicationName;

    public HealthController(
            @Value("${spring.application.name:smartspend}") String applicationName
    ) {
        this.applicationName = applicationName;
    }

    @GetMapping
    @Operation(
            summary = "Kiểm tra API",
            description = "Trả về trạng thái hoạt động hiện tại của SmartSpend API"
    )
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        Map<String, String> healthData = Map.of(
                "application", applicationName,
                "status", "UP"
        );

        ApiResponse<Map<String, String>> response =
                ApiResponse.success(
                        "SmartSpend API đang hoạt động",
                        healthData
                );

        return ResponseEntity.ok(response);
    }
}