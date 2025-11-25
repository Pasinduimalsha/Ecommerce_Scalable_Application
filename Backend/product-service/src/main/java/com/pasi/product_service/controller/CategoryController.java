package com.pasi.product_service.controller;

import com.pasi.product_service.exception.InvalidInputException;
import com.pasi.product_service.dto.CategoryDTO;
import com.pasi.product_service.exception.DuplicateResourceException;
import com.pasi.product_service.exception.InternalServerException;
import com.pasi.product_service.exception.ResourceNotFoundException;
import com.pasi.product_service.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController extends ProductAbstractController {

    private final CategoryService categoryService;

    /**
     * POST /api/v1/categories
     * Admin can add new categories
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCategory(
            @Valid @RequestBody CategoryDTO categoryDTO) 
            throws InternalServerException, DuplicateResourceException {
        log.info("Request to create category: {}", categoryDTO.getName());
        ResponseEntity<CategoryDTO> createdCategory = categoryService.createCategory(categoryDTO);
        return sendCreatedResponse(createdCategory.getBody(), "Category successfully created");
    }

    /**
     * PUT /api/v1/categories/{category_id}
     * Admin can edit category details
     */
    @PutMapping("/{categoryId}")
    public ResponseEntity<Map<String, Object>> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryDTO categoryDTO) 
            throws ResourceNotFoundException, DuplicateResourceException, InternalServerException {
        log.info("Request to update category with ID: {}", categoryId);
        ResponseEntity<CategoryDTO> updatedCategory = categoryService.updateCategory(categoryId, categoryDTO);
        return sendSuccessResponse(updatedCategory.getBody(), "Category updated successfully");
    }

    /**
     * DELETE /api/v1/categories/{category_id}
     * Admin can delete category by category_id
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable Long categoryId) 
            throws ResourceNotFoundException, InternalServerException {
        log.info("Request to delete category with ID: {}", categoryId);
        categoryService.deleteCategory(categoryId);
        return createSuccessResponse("Category deleted successfully", null);
    }

    /**
     * GET /api/v1/categories
     * Admin can view all categories
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCategories()
            throws InternalServerException {
        log.info("Request to get all categories");
        ResponseEntity<List<CategoryDTO>> categories = categoryService.getAllCategories();
        return sendSuccessResponse(categories.getBody(), "Categories retrieved successfully");
    }

    /**
     * GET /api/v1/categories/{category_id}
     * Get category by ID
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<Map<String, Object>> getCategoryById(@PathVariable Long categoryId) 
            throws ResourceNotFoundException, InternalServerException, InvalidInputException, IllegalStateException {
        log.info("Request to get category by ID: {}", categoryId);
        ResponseEntity<CategoryDTO> category = categoryService.getCategoryById(categoryId);
        return sendSuccessResponse(category.getBody(), "Category retrieved successfully");
    }
}