package com.shop.inventory.controller;

import com.shop.inventory.dto.InventoryRequest;
import com.shop.inventory.dto.InventoryResponse;
import com.shop.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getInventory(@RequestParam("sku-code") List<String> skuCodes) {
        return new ResponseEntity<>(inventoryService.getInventory(skuCodes), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createInventory(@RequestBody InventoryRequest inventoryDto) {
        try {
            inventoryService.createInventory(inventoryDto);
            return new ResponseEntity<>(inventoryDto.getQuantity() + " of " + inventoryDto.getSkuCode() + " created sucessfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Could not create " + inventoryDto.getQuantity() + " of " + inventoryDto.getSkuCode() + "! Reason: " + e.getMessage(), HttpStatus.INSUFFICIENT_STORAGE);
        }
    }
}
