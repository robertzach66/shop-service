package com.shop.order.dto;


import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequest {
    private String skuCode;
    private BigDecimal price;
    private Integer quantity;
}
