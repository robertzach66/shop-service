package com.shop.order;

import com.shop.order.dto.CustomerDto;
import com.shop.order.dto.OrderDto;
import com.shop.order.dto.OrderItemDto;
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
        final OrderItemDto orderItem1 = new OrderItemDto(null, "sku-code-mercedes-001", new BigDecimal(111), 5);
        final OrderItemDto orderItem2 = new OrderItemDto(null, "sku-code-audi-001", new BigDecimal(222), 2);
        final CustomerDto customerDto = new CustomerDto(null, null, "Robert", "Zach", "robert.zach@live.de");
        final OrderDto orderRequest = new OrderDto(null, null, null, List.of(orderItem1, orderItem2), customerDto);

        InventoryClientStup.stubInventoryCall(orderItem1.skuCode(), orderItem1.quantity());
        InventoryClientStup.stubInventoryCall(orderItem2.skuCode(), orderItem2.quantity());

        EntityExchangeResult<OrderDto> result = webTestClient.post()
                .uri("/api/order")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(orderRequest), OrderDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(OrderDto.class)
                .returnResult();

        OrderDto orderResponse = result.getResponseBody();
        Assertions.assertNotNull(orderResponse);
        Assertions.assertNotNull(orderResponse.id());
        Assertions.assertNotNull(orderResponse.orderNumber());
        Assertions.assertEquals(2, orderResponse.orderItems().size());
        Assertions.assertNotNull(orderResponse.orderItems().get(0).id());
        Assertions.assertNotNull(orderResponse.orderItems().get(1).id());
    }

    @Test
    @Order(value = 3)
    void shouldCreateOrder() {
        final OrderItemDto orderItem1 = new OrderItemDto(null, "sku-code-mercedes-001", new BigDecimal(111), 5);
        final OrderItemDto orderItem2 = new OrderItemDto(null, "sku-code-audi-001", new BigDecimal(222), 2);
        final CustomerDto customerDto = new CustomerDto(null, null, "Robert", "Zach", "robert.zach@live.de");
        final OrderDto orderRequest = new OrderDto(null, null, null, List.of(orderItem1, orderItem2), customerDto);

        EntityExchangeResult<OrderDto> result = webTestClient.post()
                .uri("/api/order/create")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(orderRequest), OrderDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(OrderDto.class)
                .returnResult();

        OrderDto orderResponse = result.getResponseBody();
        Assertions.assertNotNull(orderResponse);
        Assertions.assertNotNull(orderResponse.id());
        Assertions.assertNotNull(orderResponse.orderNumber());
        Assertions.assertEquals(2, orderResponse.orderItems().size());
        Assertions.assertNotNull(orderResponse.orderItems().get(0).id());
        Assertions.assertNotNull(orderResponse.orderItems().get(1).id());
    }
}
