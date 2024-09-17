package com.shop.inventory.service;

import com.shop.inventory.dto.InventoryRequest;
import com.shop.inventory.dto.InventoryResponse;
import com.shop.inventory.model.Inventory;
import com.shop.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventory(final List<String> skuCodes) {
        return inventoryRepository.findBySkuCodes(skuCodes)
                .stream()
                .map(this::mapEntityToDto)
                .toList();
    }

    @Transactional
    public void createInventory(final InventoryRequest inventoryDto) {
        Inventory inventory = new Inventory();
        inventory.setSkuCode(inventoryDto.getSkuCode());
        inventory.setQuantity(inventoryDto.getQuantity());
        inventoryRepository.save(inventory);
    }

    private InventoryResponse mapEntityToDto(final Inventory inventory) {
        return InventoryResponse.builder()
                .skuCode(inventory.getSkuCode())
                .isInStock(inventory.getQuantity() > 0)
                .build();
    }
}
