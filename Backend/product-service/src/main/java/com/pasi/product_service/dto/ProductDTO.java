package com.pasi.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    
    private Long id;
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    private Long categoryId; // For internal use only
    
    @NotBlank(message = "Category name is required")
    private String categoryName; // For user input and display
    
    @NotBlank(message = "Brand is required")
    private String brand;
    
    private String imageUrl;

    @NotNull(message = "Sku code is required")
    private String sku;

    private String status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}