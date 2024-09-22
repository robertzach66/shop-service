package com.shop.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class InventoryResponse {
    private Long id;
    private String skuCode;
    private Integer quantity;
    private boolean isInStock;
}
