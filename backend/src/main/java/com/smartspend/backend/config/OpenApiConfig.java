package com.smartspend.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartSpendOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartSpend API")
                        .version("v1")
                        .description("REST API for SmartSpend personal finance management.")
                        .contact(new Contact()
                                .name("SmartSpend Team")));
    }
}
