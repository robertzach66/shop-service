package com.shop.product;

import com.shop.product.model.Product;
import com.shop.product.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductRepositoryInegrationTest extends AbstractPostgresContainerIntegrationTest {

    @Autowired
    private ProductRepository repository;

    @Test
    @Order(value = 1)
    void testConnection() {
        Assertions.assertTrue(postgreSQLContainer.isRunning());
        Assertions.assertNotNull(repository, "Connection should exists");
    }

    @Test
    @Order(value = 2)
    void shouldCreateProduct() {
        Product testProduct = Product.builder()
                .name("Test-Product")
                .description("Test-description")
                .price(new BigDecimal(4711))
                .build();
        repository.save(testProduct);
        Assertions.assertNotNull(testProduct.getId());
    }

    @Test
    @Order(value = 3)
    void shouldFindById() {
        Product foundProduct = repository.findByName("Test-Product");
        Assertions.assertNotNull(foundProduct);
    }
}
