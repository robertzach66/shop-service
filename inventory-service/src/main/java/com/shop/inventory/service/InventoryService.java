package com.shop.inventory.service;

import com.shop.inventory.dto.InventoryDto;
import com.shop.inventory.model.Inventory;
import com.shop.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public boolean isInStock(final String skuCode) {
        return inventoryRepository.findBySkuCode(skuCode).isPresent();
    }

    @Transactional
    public void createInventory(final InventoryDto inventoryDto) {
        Inventory inventory = new Inventory();
        inventory.setSkuCode(inventoryDto.getSkuCode());
        inventory.setQuantity(inventoryDto.getQuantity());
        inventoryRepository.save(inventory);
    }
}
