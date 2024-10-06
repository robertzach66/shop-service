package com.shop.order.dto;

import java.util.List;

public record OrderDto(Long id, String orderNumber, String orderDate, List<OrderItemDto> orderItems, CustomerDto customer) {
}
