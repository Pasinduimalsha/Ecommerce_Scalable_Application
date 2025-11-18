package com.sysco.inventory_service.controller;

import com.sysco.inventory_service.dto.CreateInventoryRequest;
import com.sysco.inventory_service.dto.InventoryDTO;
import com.sysco.inventory_service.dto.UpdateInventoryRequest;
import com.sysco.inventory_service.exception.InventoryAlreadyExistsException;
import com.sysco.inventory_service.exception.InventoryNotFoundException;
import com.sysco.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class InventoryController extends InventoryAbstractController {

    private final InventoryService inventoryService;

    /**
     * Create inventory for a product - this is the main method to be called from product service
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createInventoryForProduct(@Valid @RequestBody CreateInventoryRequest request) {
        try {
            InventoryDTO createdInventory = inventoryService.createInventoryForProduct(
                    request.getSku(), 
                    request.getQuantity()
            );
            return sendCreatedResponse(createdInventory, "Inventory created successfully for product SKU: " + request.getSku());
        } catch (InventoryAlreadyExistsException e) {
            log.error("Error creating inventory: {}", e.getMessage());
            return sendConflictResponse("Inventory already exists for product SKU: " + request.getSku());
        } catch (Exception e) {
            log.error("Unexpected error creating inventory: {}", e.getMessage());
            return sendInternalServerErrorResponse("Failed to create inventory due to an unexpected error");
        }
    }

    /**
     * Get inventory by SKU
     */
    @GetMapping("/{sku}")
    public ResponseEntity<Map<String, Object>> getInventoryBySku(@PathVariable String sku) {
        try {
            InventoryDTO inventory = inventoryService.getInventoryBySku(sku);
            return sendSuccessResponse(inventory, "Inventory retrieved successfully for SKU: " + sku);
        } catch (InventoryNotFoundException e) {
            log.error("Inventory not found: {}", e.getMessage());
            return sendNotFoundResponse("Inventory not found for SKU: " + sku);
        } catch (Exception e) {
            log.error("Unexpected error retrieving inventory: {}", e.getMessage());
            return sendInternalServerErrorResponse("Failed to retrieve inventory due to an unexpected error");
        }
    }

    /**
     * Update inventory quantity
     */
    @PutMapping("/{sku}")
    public ResponseEntity<Map<String, Object>> updateInventoryQuantity(
            @PathVariable String sku, 
            @Valid @RequestBody UpdateInventoryRequest request) {
        try {
            InventoryDTO updatedInventory = inventoryService.updateInventoryQuantity(sku, request.getQuantity());
            return sendSuccessResponse(updatedInventory, "Inventory quantity updated successfully for SKU: " + sku);
        } catch (InventoryNotFoundException e) {
            log.error("Inventory not found: {}", e.getMessage());
            return sendNotFoundResponse("Inventory not found for SKU: " + sku);
        } catch (Exception e) {
            log.error("Unexpected error updating inventory: {}", e.getMessage());
            return sendInternalServerErrorResponse("Failed to update inventory due to an unexpected error");
        }
    }

    /**
     * Check if inventory exists for a SKU
     */
    @GetMapping("/{sku}/exists")
    public ResponseEntity<Map<String, Object>> checkInventoryExists(@PathVariable String sku) {
        try {
            boolean exists = inventoryService.inventoryExists(sku);
            return sendSuccessResponse(Map.of("exists", exists), "Inventory existence check completed for SKU: " + sku);
        } catch (Exception e) {
            log.error("Unexpected error checking inventory existence: {}", e.getMessage());
            return sendInternalServerErrorResponse("Failed to check inventory existence due to an unexpected error");
        }
    }

    /**
     * Get all inventories
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllInventories() {
        try {
            List<InventoryDTO> inventories = inventoryService.getAllInventories();
            return sendSuccessResponse(inventories, "All inventories retrieved successfully");
        } catch (Exception e) {
            log.error("Unexpected error retrieving all inventories: {}", e.getMessage());
            return sendInternalServerErrorResponse("Failed to retrieve inventories due to an unexpected error");
        }
    }
}
