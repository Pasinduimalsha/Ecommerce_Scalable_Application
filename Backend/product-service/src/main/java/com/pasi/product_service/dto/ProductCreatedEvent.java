package com.pasi.product_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreatedEvent {
    @NotNull(message = "Sku code is required")
    private String sku;

    @NotNull(message = "Stock quantity is required")
    private Integer stockQuantity;
}