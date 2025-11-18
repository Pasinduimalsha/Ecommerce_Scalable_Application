package com.sysco.inventory_service.listener;

import com.sysco.inventory_service.dto.CreateInventoryRequest;
import com.sysco.inventory_service.dto.ProductCreatedEvent;
import com.sysco.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventListener {

    private final InventoryService inventoryService;

    @RabbitListener(queues = "${app.rabbitmq.product.queue}")
    public void handleProductCreatedEvent(ProductCreatedEvent event) {
        try {
            log.info("Received ProductCreatedEvent for SKU: {} with Product ID: {}", 
                    event.getSku(), event.getProductId());

            // Check if inventory already exists to avoid duplicates
            if (inventoryService.inventoryExists(event.getSku())) {
                log.warn("Inventory already exists for SKU: {}, skipping creation", event.getSku());
                return;
            }

            // Create inventory record using the service method
            Integer quantity = event.getInitialQuantity() != null ? event.getInitialQuantity() : 0;
            inventoryService.createInventoryForProduct(event.getSku(), quantity);

            log.info("Successfully created inventory for SKU: {} with quantity: {}", 
                    event.getSku(), quantity);

        } catch (Exception e) {
            log.error("Failed to process ProductCreatedEvent for SKU: {} - Error: {}", 
                    event.getSku(), e.getMessage(), e);
            // Message will be sent to dead letter queue for manual processing
            throw e; // Re-throw to trigger dead letter queue
        }
    }
}