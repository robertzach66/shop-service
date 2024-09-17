package com.shop.order.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderRequest {
    private List<OrderItemDto> orderItems;
}
