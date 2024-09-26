package com.shop.product.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productServiceApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Service API")
                        .description("REST API for Product-Service")
                        .version("1.0.0")
                        .license(new License().name("Shop 1.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("Further Information in Product Service Wiki")
                        .url("https://product-service-wiki.com/docs"));
    }
}
