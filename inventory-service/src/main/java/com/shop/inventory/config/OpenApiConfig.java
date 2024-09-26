package com.shop.inventory.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI inventoryServiceApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory Service API")
                        .description("REST API for Inventory-Service")
                        .version("1.0.0")
                        .license(new License().name("Shop 1.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("Further Information in Inventory Service Wiki")
                        .url("https://inventory-service-wiki.com/docs"));
    }
}
