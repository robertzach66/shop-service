package com.shop.inventory.service;

import com.shop.inventory.dto.InventoryRequest;
import com.shop.inventory.dto.InventoryResponse;
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
    public List<InventoryResponse> getInventory(final List<String> skuCodes) {
        log.info("Look up {} products in inventory", skuCodes.size());

        // check for the quantities
        List<InventoryResponse> inventories = new ArrayList<>(inventoryRepository.findBySkuCodeIn(skuCodes)
                .stream()
                .map(this::mapEntityToDto)
                .toList());

        // skuCode existiert nicht
        if (!inventories.isEmpty() && inventories.size() < skuCodes.size()) {
            List<String> foundSkuCodes = inventories.stream().map(InventoryResponse::getSkuCode).toList();
            for (String skuCode : skuCodes) {
                if (!foundSkuCodes.contains(skuCode)) {
                    inventories.add(new InventoryResponse(skuCode, false));
                }
            }
        }

        return inventories;
    }

    @Transactional
    public void createInventory(final InventoryRequest inventoryDto) {
        Inventory inventory = new Inventory();
        inventory.setSkuCode(inventoryDto.getSkuCode());
        inventory.setQuantity(inventoryDto.getQuantity());
        inventoryRepository.save(inventory);
    }

    private InventoryResponse mapEntityToDto(final Inventory inventory) {
        log.info("Map Entity to Dto for: {}", inventory.getSkuCode());
        return InventoryResponse.builder()
                .skuCode(inventory.getSkuCode())
                .isInStock(inventory.getQuantity() > 0)
                .build();
    }
}
