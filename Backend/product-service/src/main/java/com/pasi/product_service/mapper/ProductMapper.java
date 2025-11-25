package com.pasi.product_service.mapper;

import com.pasi.product_service.dto.ProductDTO;
import com.pasi.product_service.entity.Category;
import com.pasi.product_service.entity.Product;
import com.pasi.product_service.exception.ResourceNotFoundException;
import com.pasi.product_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final CategoryRepository categoryRepository;

    public ProductDTO toCreatedProduct(Product product) {
        if (product == null) {
            return null;
        }
        
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .brand(product.getBrand())
                .imageUrl(product.getImageUrl())
                .sku(product.getSku())
                .stockQuantity(product.getStockQuantity())
                .status(product.getStatus() != null ? product.getStatus().name() : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public Product toCreateProduct(ProductDTO productDTO) {
        if (productDTO == null) {
            return null;
        }
        
        Product.ProductBuilder builder = Product.builder()
                .id(productDTO.getId())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .brand(productDTO.getBrand())
                .imageUrl(productDTO.getImageUrl())
                .sku(productDTO.getSku())
                .stockQuantity(productDTO.getStockQuantity())
                .createdAt(productDTO.getCreatedAt())
                .updatedAt(productDTO.getUpdatedAt());
        
        // Set category by name instead of ID
        if (productDTO.getCategoryName() != null && !productDTO.getCategoryName().trim().isEmpty()) {
            Category category = categoryRepository.findByName(productDTO.getCategoryName().trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + productDTO.getCategoryName()));
            builder.category(category);
        }
        
        if (productDTO.getStatus() != null) {
            try {
                builder.status(Product.Status.valueOf(productDTO.getStatus()));
            } catch (IllegalArgumentException e) {
                // Default to PENDING if invalid status
                builder.status(Product.Status.PENDING);
            }
        }
        
        return builder.build();
    }

}