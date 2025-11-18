package com.sysco.product_service.service;

import com.sysco.product_service.dto.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.product.routing-key}")
    private String productRoutingKey;

    /**
     * Publishes a ProductCreatedEvent to RabbitMQ
     * This will trigger inventory creation in the inventory-service
     */
    public void publishProductCreatedEvent(ProductCreatedEvent event) {
        try {
            log.info("Publishing ProductCreatedEvent for SKU: {} to exchange: {} with routing key: {}", 
                    event.getSku(), exchange, productRoutingKey);
            
            rabbitTemplate.convertAndSend(exchange, productRoutingKey, event);
            
            log.info("Successfully published ProductCreatedEvent for SKU: {} with Product ID: {}", 
                    event.getSku(), event.getProductId());
                    
        } catch (Exception e) {
            log.error("Failed to publish ProductCreatedEvent for SKU: {} - Error: {}", 
                    event.getSku(), e.getMessage(), e);
            // In a production environment, you might want to:
            // 1. Retry publishing
            // 2. Store failed events for later processing
            // 3. Send alerts to monitoring systems
            throw new RuntimeException("Failed to publish product created event", e);
        }
    }
}