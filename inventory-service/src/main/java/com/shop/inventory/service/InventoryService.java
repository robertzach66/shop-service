package com.shop.inventory.service;

import com.shop.inventory.dto.InventoryDto;
import com.shop.inventory.model.Inventory;
import com.shop.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryDto> getInventory(final List<String> skuCodes) {
        log.info("Look up {} products in inventory", skuCodes.size());

        // by query db
        skuCodes.forEach(skuCode -> log.info("{} is {}in stock!",
                skuCode,
                inventoryRepository.existsBySkuCodeAndQuantityIsGreaterThanEqual(skuCode, 0) ? "" : "not "));

        // check for the quantities
        List<InventoryDto> inventories = new ArrayList<>(inventoryRepository.findBySkuCodeIn(skuCodes)
                .stream()
                .map(this::mapEntityToDto)
                .toList());

        // skuCode existiert nicht
        if (!inventories.isEmpty() && inventories.size() < skuCodes.size()) {
            List<String> foundSkuCodes = inventories.stream().map(InventoryDto::skuCode).toList();
            for (String skuCode : skuCodes) {
                if (!foundSkuCodes.contains(skuCode)) {
                    inventories.add(new InventoryDto(null, skuCode, 0, false));
                }
            }
        }

        return inventories;
    }

    public boolean isInStock(final String skuCode, final Integer quantity) {
        return inventoryRepository.existsBySkuCodeAndQuantityIsGreaterThanEqual(skuCode, quantity);
    }

    @Transactional
    public InventoryDto createInventory(final InventoryDto inventoryDto) {
        Inventory inventory = new Inventory();
        inventory.setSkuCode(inventoryDto.skuCode());
        inventory.setQuantity(inventoryDto.quantity());
        return mapEntityToDto(inventoryRepository.save(inventory));
    }

    private InventoryDto mapEntityToDto(final Inventory inventory) {
        log.info("Map Entity to Dto for: {}", inventory.getSkuCode());
        return new InventoryDto(inventory.getId(), inventory.getSkuCode(), inventory.getQuantity(), inventory.getQuantity() > 0);
    }
}
