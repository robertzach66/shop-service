package com.shop.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.inventory.dto.InventoryRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
class InventoryControllerIntegrationTests {

    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:alpine")
            .withDatabaseName("shopdb")
            .withUsername("robert")
            .withPassword("postgreSQL66")
            .withInitScript("inventory-service-ddl.sql");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeAll
    static void beforAll() {
        postgreSQLContainer.start();
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @Order(value = 1)
    void testConnection() {
        Assertions.assertTrue(postgreSQLContainer.isRunning());
    }

    @Test
    @Order(value = 2)
    void shouldCreateInventory() throws Exception {
        final InventoryRequest inventoryRequest = InventoryRequest.builder()
                .skuCode("Test-SkuCode")
                .quantity(200)
                .build();

        String inventoryRequestString = objectMapper.writeValueAsString(inventoryRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inventoryRequestString))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(value = 2)
    void shouldFindBySkuCode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sku-code", "Test-SkuCode"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].skuCode").value("Test-SkuCode"))
                .andExpect(jsonPath("$[0].quantity").value(200));
    }

}
