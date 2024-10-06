package com.shop.order.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private List<OrderItemRequest> orderItems;
    private CustomerRequest customer;
}
