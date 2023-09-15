package com.klasha.assessment.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Klasha assessment API")
                        .version("v1.0"));
//                .components((new Components()).addSecuritySchemes("bearer-key", (new SecurityScheme())
//                        .type(SecurityScheme.Type.HTTP).scheme("bearer")))
//                .addSecurityItem((new SecurityRequirement()).addList("bearer-key", Arrays.asList(new String[] { "read", "write" })));
    }
}