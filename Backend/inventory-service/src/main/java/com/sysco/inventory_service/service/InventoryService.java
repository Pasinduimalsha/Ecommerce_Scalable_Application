package com.sysco.inventory_service.service;

import com.sysco.inventory_service.dto.InventoryDTO;

import java.util.List;

public interface InventoryService {
    
    /**
     * Create inventory for a new product
     * This method should be called when a product is created in the product service
     */
    InventoryDTO createInventoryForProduct(String sku, Integer quantity);
    
    /**
     * Get inventory by SKU
     */
    InventoryDTO getInventoryBySku(String sku);
    
    /**
     * Update inventory quantity
     */
    InventoryDTO updateInventoryQuantity(String sku, Integer quantity);
    
    /**
     * Check if inventory exists for a product SKU
     */
    boolean inventoryExists(String sku);
    
    /**
     * Get all inventories
     */
    List<InventoryDTO> getAllInventories();
    
}
