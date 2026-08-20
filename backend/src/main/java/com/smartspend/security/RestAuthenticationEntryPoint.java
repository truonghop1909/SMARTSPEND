package com.smartspend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RestAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    private static final String TRACE_ID_KEY = "traceId";

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
        );

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );

        response.setCharacterEncoding("UTF-8");

        response.setHeader(
                HttpHeaders.WWW_AUTHENTICATE,
                "Bearer"
        );

        Map<String, Object> responseBody =
                new LinkedHashMap<>();

        responseBody.put("success", false);
        responseBody.put(
                "message",
                "Authentication is required"
        );
        responseBody.put(
                "errorCode",
                "AUTH_UNAUTHORIZED"
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