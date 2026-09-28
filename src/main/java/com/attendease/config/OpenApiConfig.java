package com.attendease.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI attendEaseOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("AttendEase API").version("1.0.0")
                        .description("Faculty mark attendance; students view their own record. "
                                + "Click Authorize and enter the faculty API key (default: faculty123)."))
                .components(new Components().addSecuritySchemes("facultyKey",
                        new SecurityScheme().type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER).name("X-API-Key")))
                .addSecurityItem(new SecurityRequirement().addList("facultyKey"));
    }
}
