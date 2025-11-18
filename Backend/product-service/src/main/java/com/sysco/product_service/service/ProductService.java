package com.sysco.product_service.service;

import com.sysco.product_service.dto.ProductDTO;
import com.sysco.product_service.dto.ReviewStatusDTO;
import com.sysco.product_service.entity.Product;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
    
    // Supplier operations
    ResponseEntity<ProductDTO> createProduct(ProductDTO productDTO);
    ResponseEntity<ProductDTO> updateProduct(Long id, ProductDTO productDTO);
    ResponseEntity<Void> deleteProduct(Long id);
    
    // Customer/General operations
    ResponseEntity<ProductDTO> getProductById(Long id);
    ResponseEntity<List<ProductDTO>> getAllApprovedProducts();
    ResponseEntity<List<ProductDTO>> searchProducts(String searchValue);
    ResponseEntity<List<ProductDTO>> getProductsByCategoryName(String categoryName);
    
    // Data Steward operations
    ResponseEntity<ProductDTO> reviewProduct(ReviewStatusDTO reviewStatusDTO);
    
    // Admin/Data Steward operations
    ResponseEntity<List<ProductDTO>> getAllProducts();
    List<ProductDTO> getProductsByStatus(Product.Status status);
}