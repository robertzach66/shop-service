package com.shop.inventory.dto;

public record InventoryDto(Long id, String skuCode, Integer quantity, boolean isInStock) {
}
