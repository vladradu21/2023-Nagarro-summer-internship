package com.nagarro.si.pba.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@OpenAPIDefinition
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer")
public class SpringdocConfig {
    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI().info(new Info().title("Personal Budget Adviser Endpoints").version("1.0.0")
                .description("below are the endpoints for the Personal Budget Adviser application. use a valid bearer token to access the endpoints"));
    }
}
