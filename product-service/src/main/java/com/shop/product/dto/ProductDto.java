package com.shop.product.dto;

import java.math.BigDecimal;

public record ProductDto(Long id, String name, String description, String skuCode, BigDecimal price) {
}
