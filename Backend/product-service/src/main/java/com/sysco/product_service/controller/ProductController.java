package com.sysco.product_service.controller;

import com.sysco.product_service.dto.ProductDTO;
import com.sysco.product_service.dto.ReviewStatusDTO;
import com.sysco.product_service.entity.Product;
import com.sysco.product_service.exception.*;
import com.sysco.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController extends ProductAbstractController {

    private final ProductService productService;

    /**
     * GET /api/v1/products
     * Customer/Supplier/Data Steward - All parties can view approved list of items
     */
    @GetMapping("approved")
    public ResponseEntity<Map<String, Object>> getAllApprovedProducts() 
            throws InternalServerException {
        log.info("Request to get all approved products");
        ResponseEntity<List<ProductDTO>> products = productService.getAllApprovedProducts();
        return sendSuccessResponse(products.getBody(), "Approved products retrieved successfully");
    }

    /**
     * GET /api/v1/products/{product_id}
     * Customer/Supplier - Can view product details by product_id
     */
    @GetMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long productId
    ) throws ProductNotFoundException, InvalidInputException, InternalServerException {
        log.info("Request to get product by ID: {}", productId);
        ResponseEntity<ProductDTO> product = productService.getProductById(productId);
        return sendSuccessResponse(product.getBody(), "Product retrieved successfully");
    }

    /**
     * GET /api/v1/products?search={search_value}
     * Customer/Admin - Can search product by product_name, category_name
     */
    @GetMapping(params = "search")
    public ResponseEntity<Map<String, Object>> searchProducts(@RequestParam("search") String searchValue)
            throws InvalidInputException, InternalServerException {
        log.info("Search request with value: {}", searchValue);
        ResponseEntity<List<ProductDTO>> serviceResponse = productService.searchProducts(searchValue);
        
        List<ProductDTO> products = serviceResponse.getBody();
        if (products == null || products.isEmpty()) {
            return sendSuccessResponse(
                    List.of(),
                    "No products found matching search criteria: '" + searchValue.trim() + "'"
            );
        }

        log.info("Search completed successfully. Found {} products for search term: '{}'",
                products.size(), searchValue.trim());

        return sendSuccessResponse(products,
                "Found " + products.size() + " product(s) matching search criteria");
    }

    /**
     * GET /api/v1/products/categories/{category_name}
     * Customer - Can view products by category_name
     */
    @GetMapping("/categories/{categoryName}")
    public ResponseEntity<Map<String, Object>> getProductsByCategoryName(@PathVariable String categoryName)
            throws InvalidInputException, InternalServerException {
        log.info("Request to get products by category: {}", categoryName);
        ResponseEntity<List<ProductDTO>> products = productService.getProductsByCategoryName(categoryName);
        return sendSuccessResponse(products.getBody(), "Products retrieved by category successfully");
    }

    /**
     * POST /api/v1/products
     * Supplier - Add products for review
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@Valid @RequestBody ProductDTO productDTO
    ) throws InternalServerException, ProductCreationException, DuplicateSkuException {
        log.info("Request to create product: {}", productDTO.getName());
        ResponseEntity<ProductDTO> createdProduct = productService.createProduct(productDTO);
        return sendCreatedResponse(createdProduct.getBody(), "Product successfully created and submitted for review");
    }

    /**
     * PUT /api/v1/products/{product_id}
     * Supplier - Edit product details before approval
     */
    @PutMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductDTO productDTO
    ) throws ResourceNotFoundException, DuplicateSkuException, InternalServerException {
        log.info("Request to update product with ID: {}", productId);
        ResponseEntity<ProductDTO> updatedProduct = productService.updateProduct(productId, productDTO);
        return sendSuccessResponse(updatedProduct.getBody(), "Product updated successfully");
    }

    /**
     * DELETE /api/v1/products/{product_id}
     * Supplier - Remove products before approval
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long productId
    ) throws ResourceNotFoundException, InternalServerException {
        log.info("Request to delete product with ID: {}", productId);
        productService.deleteProduct(productId);
        return sendSuccessResponse(null, "Product deleted successfully");
    }

    /**
     * PUT /api/v1/products/{product_id}/review?status=APPROVED
     * Data Steward - Can approve or reject products with review details in body
     */
    @PutMapping("/{productId}/review")
    public ResponseEntity<Map<String, Object>> reviewProduct(
            @PathVariable Long productId,
            @RequestParam("status") String status,
            @Valid @RequestBody ReviewStatusDTO reviewStatusDTO
    ) throws ProductNotFoundException, InvalidInputException, InternalServerException {
        log.info("Request to review product with ID: {} to status: {}", productId, status);
        
        // Validate status parameter
        if (!status.equalsIgnoreCase("APPROVED") && !status.equalsIgnoreCase("REJECTED")) {
            throw new InvalidInputException("Status must be either APPROVED or REJECTED");
        }
        
        // Set the product ID and status from URL parameters
        reviewStatusDTO.setProductId(productId);
        reviewStatusDTO.setStatus(status.toUpperCase());
        
        ResponseEntity<ProductDTO> reviewedProduct = productService.reviewProduct(reviewStatusDTO);
        return sendSuccessResponse(reviewedProduct.getBody(), "Product reviewed successfully");
    }

    /**
     * GET /api/v1/products
     * Admin/Data Steward - Can view all products regardless of status
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts()
            throws InternalServerException {
        log.info("Request to get all products (admin/data steward)");
        ResponseEntity<List<ProductDTO>> products = productService.getAllProducts();
        return sendSuccessResponse(products.getBody(), "All products retrieved successfully");
    }

    /**
     * GET /api/v1/products/status/{status}
     * Admin/Data Steward - Get products by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getProductsByStatus(@PathVariable String status) 
            throws InvalidInputException, InternalServerException {
        log.info("Request to get products by status: {}", status);
        
        // Validate status
        Product.Status productStatus;
        try {
            productStatus = Product.Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid status: " + status + ". Valid statuses are: PENDING, APPROVED, REJECTED");
        }
        
        List<ProductDTO> products = productService.getProductsByStatus(productStatus);
        return sendSuccessResponse(products, "Products retrieved by status successfully");
    }

}