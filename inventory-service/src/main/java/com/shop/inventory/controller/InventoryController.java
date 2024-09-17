package com.shop.inventory.controller;

import com.shop.inventory.dto.InventoryDto;
import com.shop.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{sku-code}")
    public ResponseEntity<Boolean> isInStock(@PathVariable("sku-code") String skuCode) {
        return new ResponseEntity<>(inventoryService.isInStock(skuCode), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createInventory(@RequestBody InventoryDto inventoryDto) {
        try {
            inventoryService.createInventory(inventoryDto);
            return new ResponseEntity<>(inventoryDto.getQuantity() + " of " + inventoryDto.getSkuCode() + " created sucessfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Could not create " + inventoryDto.getQuantity() + " of " + inventoryDto.getSkuCode() + "! Reason: " + e.getMessage(), HttpStatus.INSUFFICIENT_STORAGE);
        }
    }
}
