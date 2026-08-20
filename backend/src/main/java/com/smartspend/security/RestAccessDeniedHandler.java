package com.smartspend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RestAccessDeniedHandler
        implements AccessDeniedHandler {

    private static final String TRACE_ID_KEY = "traceId";

    private final ObjectMapper objectMapper;

    public RestAccessDeniedHandler(
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exception
    ) throws IOException, ServletException {
        response.setStatus(
                HttpServletResponse.SC_FORBIDDEN
        );

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );

        response.setCharacterEncoding("UTF-8");

        Map<String, Object> responseBody =
                new LinkedHashMap<>();

        responseBody.put("success", false);
        responseBody.put(
                "message",
                "You do not have permission to access this resource"
        );
        responseBody.put(
                "errorCode",
                "ACCESS_DENIED"
        );
        responseBody.put("errors", null);
        responseBody.put(
                "timestamp",
                LocalDateTime.now()
        );
        responseBody.put(
                "traceId",
                MDC.get(TRACE_ID_KEY)
        );

        objectMapper.writeValue(
                response.getOutputStream(),
                responseBody
        );
    }
}