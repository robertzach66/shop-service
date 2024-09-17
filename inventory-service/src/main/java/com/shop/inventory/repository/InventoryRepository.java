package com.shop.inventory.repository;

import com.shop.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findBySkuCodes(List<String> skuCodes);
}
