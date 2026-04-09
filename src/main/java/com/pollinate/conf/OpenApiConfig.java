package com.pollinate.conf;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Management Microservice API")
                        .description("APIs for managing products and orders")
                        .version("v1")
                        .contact(new Contact().name("Order Management Team")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .build();
    }
}

