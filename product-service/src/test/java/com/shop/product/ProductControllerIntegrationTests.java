package com.shop.product;

import com.shop.product.dto.ProductDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerIntegrationTests extends AbstractPostgresContainerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @Order(value = 1)
    void testConnection() {
        Assertions.assertTrue(postgreSQLContainer.isRunning());
        Assertions.assertNotNull(webTestClient);
    }

    @Test
    @Order(value = 2)
    void shouldCreateProduct() {
        ProductDto productRequest = new ProductDto(null, "Test-Product-XYZ", "Test-Description-XYZ", "sku-code-123", new BigDecimal(12345));

        webTestClient.post()
                .uri("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(productRequest), ProductDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Test-Product-XYZ");
    }

    @Test
    @Order(value = 3)
    void shouldFindAllProducts() {
        EntityExchangeResult<List<ProductDto>> products = webTestClient.get()
                .uri("/api/product")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ProductDto.class).hasSize(1)
                .returnResult();

        Assertions.assertEquals("Test-Product-XYZ", products.getResponseBody().get(0).name());
    }
}
