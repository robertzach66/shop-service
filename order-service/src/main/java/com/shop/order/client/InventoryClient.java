package com.shop.order.client;

import com.shop.order.dto.InventoryDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Collections;
import java.util.List;

public interface InventoryClient {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(InventoryClient.class);

    @GetExchange("/api/inventory/instock")
    @CircuitBreaker(name = "inventory", fallbackMethod = "inInStockFallback")
    @Retry(name = "inventory")
    boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity);

    @GetExchange("/api/inventory")
    @CircuitBreaker(name = "inventory", fallbackMethod = "getInventoryFallback")
    @Retry(name = "inventory")
    List<InventoryDto> getInventory(@RequestParam("sku-code") List<String> skuCodes);

    default boolean inInStockFallback(String skuCode, Integer quantity, Throwable throwable) {
        log.info("Cannot get {} of skuCode: {}. Failure reason: {}", quantity, skuCode, throwable.getMessage(), throwable);
        return false;
    }

    default List<InventoryDto>  getInventoryFallback(String skuCode, Throwable throwable) {
        log.info("Cannot get skuCode: {}, failure reason: {}", skuCode, throwable.getMessage(), throwable);
        return Collections.emptyList();
    }
}
