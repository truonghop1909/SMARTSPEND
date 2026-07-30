package com.smartspend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME =
            "Bearer Authentication";

    @Bean
    public OpenAPI smartSpendOpenApi() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(SECURITY_SCHEME_NAME)
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SECURITY_SCHEME_NAME,
                                        bearerSecurityScheme()
                                )
                );
    }

    private Info apiInfo() {
        return new Info()
                .title("SmartSpend API")
                .description(
                        "REST API cho hệ thống quản lý "
                                + "tài chính cá nhân SmartSpend"
                )
                .version("0.1.0")
                .contact(
                        new Contact()
                                .name("SmartSpend")
                );
    }

    private SecurityScheme bearerSecurityScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description(
                        "Nhập Access Token theo định dạng: Bearer {token}"
                );
    }
}