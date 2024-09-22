package com.shop.order;

import com.shop.order.dto.OrderItemRequest;
import com.shop.order.dto.OrderRequest;
import com.shop.order.dto.OrderResponse;
import com.shop.order.stub.InventoryClientStup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWireMock(port = 0)
@Slf4j
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
    void shouldPlaceOrder() {
        final OrderRequest orderRequest = OrderRequest.builder().build();
        final OrderItemRequest orderItem1 = OrderItemRequest.builder()
                .skuCode("Test-Skucode-1")
                .price(new BigDecimal(111))
                .quantity(5)
                .build();
        final OrderItemRequest orderItem2 = OrderItemRequest.builder()
                .skuCode("Test-Skucode-2")
                .price(new BigDecimal(222))
                .quantity(2)
                .build();

        final List<OrderItemRequest> orderItems = new ArrayList<>();
        orderItems.add(orderItem1);
        orderItems.add(orderItem2);
        orderRequest.setOrderItems(orderItems);

        InventoryClientStup.stubInventoryCall(orderItem1.getSkuCode(), orderItem1.getQuantity());
        InventoryClientStup.stubInventoryCall(orderItem2.getSkuCode(), orderItem2.getQuantity());

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

    @Test
    @Order(value = 3)
    void shouldCreateOrder() {
        final OrderRequest orderRequest = OrderRequest.builder().build();
        final OrderItemRequest orderItem1 = OrderItemRequest.builder()
                .skuCode("Mercedes Benz EQE")
                .price(new BigDecimal(111))
                .quantity(5)
                .build();
        final OrderItemRequest orderItem2 = OrderItemRequest.builder()
                .skuCode("Audi Quattro")
                .price(new BigDecimal(222))
                .quantity(2)
                .build();

        final List<OrderItemRequest> orderItems = new ArrayList<>();
        orderItems.add(orderItem1);
        orderItems.add(orderItem2);
        orderRequest.setOrderItems(orderItems);

        EntityExchangeResult<OrderResponse> result = webTestClient.post()
                .uri("/api/order/create")
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
