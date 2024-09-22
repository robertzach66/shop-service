package com.shop.inventory.controller;

import com.shop.inventory.dto.InventoryRequest;
import com.shop.inventory.dto.InventoryResponse;
import com.shop.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getInventory(@RequestParam("sku-code") List<String> skuCodes) {
        try {
            List<InventoryResponse> inventories = inventoryService.getInventory(skuCodes);
            if (!inventories.isEmpty()) {
                return new ResponseEntity<>(inventories, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            log.info("Colud not find Products. Reason: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/instock")
    public ResponseEntity<Boolean> isInStock(@RequestParam("skuCode") String skuCode, @RequestParam("quantity") Integer quantity) {
        return new ResponseEntity<>(inventoryService.isInStock(skuCode, quantity), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@RequestBody InventoryRequest inventoryDto) {
        try {
            return new ResponseEntity<>(inventoryService.createInventory(inventoryDto), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
