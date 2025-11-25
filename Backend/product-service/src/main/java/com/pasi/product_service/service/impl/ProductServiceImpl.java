package com.pasi.product_service.service.impl;

import com.pasi.product_service.dto.ProductCreatedEvent;
import com.pasi.product_service.dto.ProductDTO;
import com.pasi.product_service.dto.ReviewStatusDTO;
import com.pasi.product_service.entity.Product;
import com.pasi.product_service.exception.*;
import com.pasi.product_service.mapper.ProductMapper;
import com.pasi.product_service.repository.ProductRepository;
import com.pasi.product_service.service.EventPublisher;
import com.pasi.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final String PRODUCT_NOT_FOUND_MSG = "Product not found with ID: ";

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final EventPublisher eventPublisher;

    // Supplier operations
    @Override
    public ResponseEntity<ProductDTO> createProduct(ProductDTO productDTO) {
        try {
            log.info("Creating new product: {}", productDTO.getName());
            
            // Check if SKU already exists
            if (productRepository.existsBySku(productDTO.getSku())) {
                log.warn("Attempt to create product with duplicate SKU: {}", productDTO.getSku());
                throw new DuplicateSkuException("Product with SKU '" + productDTO.getSku() + "' already exists");
            }

            Product product = productMapper.toCreateProduct(productDTO);
            product.setStatus(Product.Status.PENDING); // New products start as PENDING
            
            Product savedProduct = productRepository.save(product);
            log.info("Product created successfully with ID: {}", savedProduct.getId());

            // Publish ProductCreatedEvent to RabbitMQ for inventory creation
            try {
                ProductCreatedEvent event = ProductCreatedEvent.builder()
                        .productId(savedProduct.getId().toString())
                        .sku(savedProduct.getSku())
                        .name(savedProduct.getName())
                        .price(savedProduct.getPrice())
                        .description(savedProduct.getDescription())
                        .brand(savedProduct.getBrand())
                        .categoryName(savedProduct.getCategory() != null ? savedProduct.getCategory().getName() : null)
                        .initialQuantity(savedProduct.getStockQuantity() != null ? savedProduct.getStockQuantity() : 0)
                        .timestamp(LocalDateTime.now())
                        .build();

                eventPublisher.publishProductCreatedEvent(event);
                log.info("Successfully published ProductCreatedEvent for SKU: {}", savedProduct.getSku());
                
            } catch (Exception e) {
                log.error("Failed to publish ProductCreatedEvent for SKU: {} - Error: {}", 
                         savedProduct.getSku(), e.getMessage());
                // Note: We don't fail the product creation if event publishing fails
                // The product is still created, but inventory creation might need manual intervention
            }

            ProductDTO createdProductDTO = productMapper.toCreatedProduct(savedProduct);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProductDTO);
            
        } catch (DuplicateSkuException e) {
            log.error("Duplicate SKU exception: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while creating product: {}", e.getMessage(), e);
            throw new InternalServerException("An unexpected error occurred while creating the product");
        }
    }

    @Override
    public ResponseEntity<ProductDTO> updateProduct(Long id, ProductDTO productDTO) {
        try {
            log.info("Updating product with ID: {}", id);
            
            Product existingProduct = productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MSG + id));
            
            // Only allow updates if product is still PENDING
            if (existingProduct.getStatus() != Product.Status.PENDING) {
                throw new IllegalStateException("Cannot update product that has been reviewed (status: " + existingProduct.getStatus() + ")");
            }
            
            // Check SKU uniqueness if changed
            if (!existingProduct.getSku().equals(productDTO.getSku()) && 
                productRepository.existsBySku(productDTO.getSku())) {
                throw new DuplicateSkuException("Product with SKU '" + productDTO.getSku() + "' already exists");
            }
            
            // Update product fields
            existingProduct.setName(productDTO.getName());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setBrand(productDTO.getBrand());
            existingProduct.setImageUrl(productDTO.getImageUrl());
            existingProduct.setSku(productDTO.getSku());
            existingProduct.setStockQuantity(productDTO.getStockQuantity());
            
            // Update category if provided (by name)
            if (productDTO.getCategoryName() != null && !productDTO.getCategoryName().trim().isEmpty() && 
                !existingProduct.getCategory().getName().equals(productDTO.getCategoryName().trim())) {
                Product tempProduct = productMapper.toCreateProduct(productDTO);
                existingProduct.setCategory(tempProduct.getCategory());
            }
            
            Product updatedProduct = productRepository.save(existingProduct);
            ProductDTO responseDTO = productMapper.toCreatedProduct(updatedProduct);
            
            log.info("Product updated successfully: {}", updatedProduct.getName());
            return ResponseEntity.ok(responseDTO);
            
        } catch (ResourceNotFoundException | DuplicateSkuException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating product: {}", e.getMessage(), e);
            throw new InternalServerException("An unexpected error occurred while updating the product");
        }
    }

    @Override
    public ResponseEntity<Void> deleteProduct(Long id) {
        try {
            log.info("Deleting product with ID: {}", id);
            
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MSG + id));
            
            // Only allow deletion if product is still PENDING
            if (product.getStatus() != Product.Status.PENDING) {
                throw new IllegalStateException("Cannot delete product that has been reviewed (status: " + product.getStatus() + ")");
            }
            
            productRepository.delete(product);
            
            log.info("Product deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
            
        } catch (ResourceNotFoundException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting product: {}", e.getMessage(), e);
            throw new InternalServerException("An unexpected error occurred while deleting the product");
        }
    }

    // Customer/General operations
    @Override
    public ResponseEntity<ProductDTO> getProductById(Long id) {
        try {
            log.info("Fetching product by ID: {}", id);
            
            if (id == null || id <= 0) {
                throw new InvalidInputException("Product ID must be a positive number");
            }

            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MSG + id));

            ProductDTO productDTO = productMapper.toCreatedProduct(product);
            log.info("Product retrieved successfully: {} (SKU: {})", product.getName(), product.getSku());

            return ResponseEntity.ok(productDTO);
            
        } catch (ProductNotFoundException | InvalidInputException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while fetching product with ID {}: {}", id, e.getMessage(), e);
            throw new InternalServerException("An unexpected error occurred while retrieving the product");
        }
    }

    @Override
    public ResponseEntity<List<ProductDTO>> getAllApprovedProducts() {
        try {
            log.info("Fetching all approved products");
            
            List<Product> products = productRepository.findAllApprovedProducts();
            List<ProductDTO> productDTOs = products.stream()
                    .map(productMapper::toCreatedProduct)
                    .collect(Collectors.toList());
            
            log.info("Retrieved {} approved products", productDTOs.size());
            return ResponseEntity.ok(productDTOs);
            
        } catch (Exception e) {
            log.error("Unexpected error while fetching approved products: {}", e.getMessage(), e);
            throw new InternalServerException("An unexpected error occurred while retrieving approved products");
        }
    }

    @Override
    public ResponseEntity<List<ProductDTO>> searchProducts(String searchValue) {
        try {
            log.info("Searching products with value: {}", searchValue);

            if (searchValue == null || searchValue.trim().isEmpty()) {
                throw new InvalidInputException("Search value cannot be null or empty");
            }

            String trimmedSearchValue = searchValue.trim();
            if (trimmedSearchValue.length() < 2) {
                throw new InvalidInputException("Search value must be at least 2 characters long");
            }

            List<Product> products = productRepository.searchProducts(trimmedSearchValue);
            List<ProductDTO> productDTOs = products.stream()
                    .map(productMapper::toCreatedProduct)
                    .collect(Collectors.toList());

            log.info("Found {} products matching search criteria: {}", productDTOs.size(), trimmedSearchValue);
            return ResponseEntity.ok(productDTOs);
            
        } catch (InvalidInputException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while searching products: {}", e.getMessage(), e);
            throw new InternalServerException("An unexpected error occurred while searching products");
        }
    }

    @Override
    public ResponseEntity<List<ProductDTO>> getProductsByCategoryName(String categoryName) {
        try {
            log.info("Fetching approved products by category: {}", categoryName);
            
            if (categoryName == null || categoryName.trim().isEmpty()) {
                throw new InvalidInputException("Category name cannot be null or empty");
            }
            
            List<Product> products = productRepository.findApprovedProductsByCategoryName(categoryName);
            List<ProductDTO> productDTOs = products.stream()
                    .map(productMapper::toCreatedProduct)
                    .collect(Collectors.toList());
            
            log.info("Found {} approved products in category: {}", productDTOs.size(), categoryName);
            return ResponseEntity.ok(productDTOs);
            
        } catch (InvalidInputException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while fetching products by category: {}", e.getMessage(), e);
            throw new InternalServerException("An unexpected error occurred while retrieving products by category");
        }
    }

    // Data Steward operations
    @Override
    public ResponseEntity<ProductDTO> reviewProduct(ReviewStatusDTO reviewStatusDTO) {
        try {
            log.info("Reviewing product with ID: {} to status: {} by reviewer: {}", 
                    reviewStatusDTO.getProductId(), reviewStatusDTO.getStatus(), reviewStatusDTO.getReviewedBy());

            Product product = productRepository.findById(reviewStatusDTO.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MSG + reviewStatusDTO.getProductId()));

            // Validate status (validation is already done by @Pattern annotation, but double-check)
            Product.Status newStatus;
            try {
                newStatus = Product.Status.valueOf(reviewStatusDTO.getStatus().toUpperCase());
                if (newStatus == Product.Status.PENDING) {
                    throw new InvalidInputException("Cannot review product back to PENDING status");
                }
            } catch (IllegalArgumentException e) {
                throw new InvalidInputException("Invalid status: " + reviewStatusDTO.getStatus() + ". Valid statuses are: APPROVED, REJECTED");
            }

            // Update product status
            product.setStatus(newStatus);
            
            // TODO: In a real application, you might want to store the review comment and reviewer info
            // This could be done by adding fields to the Product entity or creating a separate ProductReview entity
            
            Product savedProduct = productRepository.save(product);
            
            log.info("Product ID: {} status updated to: {} with comment: '{}' by reviewer: {}", 
                    reviewStatusDTO.getProductId(), newStatus, 
                    reviewStatusDTO.getReviewComment(), reviewStatusDTO.getReviewedBy());

            ProductDTO productDTO = productMapper.toCreatedProduct(savedProduct);
            return ResponseEntity.ok(productDTO);
            
        } catch (ProductNotFoundException | InvalidInputException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while reviewing product: {}", e.getMessage(), e);
            throw new InternalServerException("An unexpected error occurred while reviewing the product");
        }
    }

    // Admin/Data Steward operations
    @Override
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        try {
            log.info("Fetching all products");
            
            List<Product> products = productRepository.findAll();
            List<ProductDTO> productDTOs = products.stream()
                    .map(productMapper::toCreatedProduct)
                    .toList();
            
            log.info("Retrieved {} products", productDTOs.size());
            return ResponseEntity.ok(productDTOs);
            
        } catch (Exception e) {
            log.error("Unexpected error while fetching all products: {}", e.getMessage(), e);
            throw new InternalServerException("An unexpected error occurred while retrieving products");
        }
    }

    @Override
    public List<ProductDTO> getProductsByStatus(Product.Status status) {
        try {
            log.info("Fetching products by status: {}", status);
            
            List<Product> products = productRepository.findByStatus(status);
            List<ProductDTO> productDTOs = products.stream()
                    .map(productMapper::toCreatedProduct)
                    .collect(Collectors.toList());
            
            log.info("Found {} products with status: {}", productDTOs.size(), status);
            return productDTOs;
            
        } catch (Exception e) {
            log.error("Error while fetching products by status {}: {}", status, e.getMessage(), e);
            throw new InternalServerException("Failed to retrieve products by status");
        }
    }
}