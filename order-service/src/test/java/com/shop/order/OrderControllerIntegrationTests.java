package com.shop.order;

import com.shop.order.dto.OrderItemRequest;
import com.shop.order.dto.OrderRequest;
import com.shop.order.dto.OrderResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderControllerIntegrationTests extends AbstractPostgresContainerIntegrationTest {

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
    void shouldCreateOrder() {
        final OrderRequest orderRequest = OrderRequest.builder().build();
        final OrderItemRequest orderItem1 = OrderItemRequest.builder()
                .skuCode("Test-SkuCode-1")
                .price(new BigDecimal(111))
                .quantity(1)
                .build();
        final OrderItemRequest orderItem2 = OrderItemRequest.builder()
                .skuCode("Test-SkuCode-1")
                .price(new BigDecimal(222))
                .quantity(2)
                .build();

        final List<OrderItemRequest> orderItems = new ArrayList<>();
        orderItems.add(orderItem1);
        orderItems.add(orderItem2);
        orderRequest.setOrderItems(orderItems);

        EntityExchangeResult<OrderResponse> result = webTestClient.post()
                .uri("/api/order")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(orderRequest), OrderRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(OrderResponse.class)
                .returnResult();

        OrderResponse orderResponse = result.getResponseBody();
        Assertions.assertNotNull(orderResponse);
        Assertions.assertNotNull(orderResponse.getId());
        Assertions.assertNotNull(orderResponse.getOrderNumber());
        Assertions.assertEquals(2, orderResponse.getOrderItems().size());
        Assertions.assertNotNull(orderResponse.getOrderItems().get(0).getId());
        Assertions.assertNotNull(orderResponse.getOrderItems().get(1).getId());
    }
}
