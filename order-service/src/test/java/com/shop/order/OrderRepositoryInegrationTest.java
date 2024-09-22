package com.shop.order;

import com.shop.order.model.OrderItem;
import com.shop.order.model.Ordering;
import com.shop.order.repository.OrderRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderRepositoryInegrationTest extends AbstractPostgresContainerIntegrationTest {

	@Autowired
	private OrderRepository repository;

	@Test
	@Order(value = 1)
	void testConnection() {
		Assertions.assertTrue(postgreSQLContainer.isRunning());
		Assertions.assertNotNull(repository, "Connection should exists");
	}

	@Test
	@Order(value = 2)
	void shouldCreateOrder() {
		final Ordering ordering = Ordering.builder().build();
		final OrderItem orderItem1 = OrderItem.builder()
				.skuCode("Test-SkuCode-1")
				.price(new BigDecimal(111))
				.ordering(ordering)
				.build();
		final OrderItem orderItem2 = OrderItem.builder()
				.skuCode("Test-SkuCode-1")
				.price(new BigDecimal(222))
				.ordering(ordering)
				.build();

		final List<OrderItem> orderItems = new ArrayList<>();
		orderItems.add(orderItem1);
		orderItems.add(orderItem2);
		ordering.setOrderItems(orderItems);

		repository.save(ordering);
		Assertions.assertNotNull(ordering.getId());
		// Assertions.assertNotNull(ordering.getOrderNumber());
		Assertions.assertEquals(2, ordering.getOrderItems().size());
		Assertions.assertNotNull(ordering.getOrderItems().get(0));
		Assertions.assertNotNull(ordering.getOrderItems().get(1));
		Assertions.assertNotNull(ordering.getOrderItems().get(0).getId());
		Assertions.assertNotNull(ordering.getOrderItems().get(1).getId());
	}
}
