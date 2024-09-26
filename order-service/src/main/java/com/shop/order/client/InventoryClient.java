package com.shop.order.client;

import com.shop.order.dto.InventoryResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

public interface InventoryClient {

    @GetExchange("/api/inventory/instock")
    boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity);

    @GetExchange("/api/inventory")
    List<InventoryResponse> getInventory(@RequestParam("sku-code") List<String> skuCodes);
}
