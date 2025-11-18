package com.sysco.inventory_service.service.impl;

import com.sysco.inventory_service.dto.InventoryDTO;
import com.sysco.inventory_service.entity.Inventory;
import com.sysco.inventory_service.exception.InventoryAlreadyExistsException;
import com.sysco.inventory_service.exception.InventoryNotFoundException;
import com.sysco.inventory_service.mapper.InventoryMapper;
import com.sysco.inventory_service.repository.InventoryRepository;
import com.sysco.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    public InventoryDTO createInventoryForProduct(String sku, Integer quantity) {
        log.info("Creating inventory for SKU: {}, Quantity: {}", sku, quantity);
        
        // Check if inventory already exists for this product
        if (inventoryRepository.existsBySku(sku)) {
            log.warn("Inventory already exists for sku: {}", sku);
            throw new InventoryAlreadyExistsException("Inventory already exists for sku: " + sku);
        }
        
        // Create new inventory
        Inventory inventory = new Inventory();
        inventory.setSku(sku);
        inventory.setQuantity(quantity);
        
        Inventory savedInventory = inventoryRepository.save(inventory);
        log.info("Successfully created inventory with ID: {} for sku ID: {}", savedInventory.getId(), savedInventory.getSku());
        
        return inventoryMapper.toDTO(savedInventory);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryDTO getInventoryBySku(String sku) {
        log.info("Fetching inventory for SKU: {}", sku);
        
        Inventory inventory = inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for sku: " + sku));
        
        return inventoryMapper.toDTO(inventory);
    }

    @Override
    public InventoryDTO updateInventoryQuantity(String sku, Integer quantity) {
        log.info("Updating inventory quantity for SKU: {} to {}", sku, quantity);
        
        Inventory inventory = inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for sku: " + sku));
        
        inventory.setQuantity(quantity);
        Inventory savedInventory = inventoryRepository.save(inventory);
        
        log.info("Successfully updated inventory quantity for SKU: {}", sku);
        return inventoryMapper.toDTO(savedInventory);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean inventoryExists(String sku) {
        return inventoryRepository.existsBySku(sku);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDTO> getAllInventories() {
        log.info("Fetching all inventories");
        
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream()
                .map(inventoryMapper::toDTO)
                .collect(Collectors.toList());
    }
}
