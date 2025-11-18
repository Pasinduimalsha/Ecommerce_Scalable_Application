package com.sysco.product_service.service.impl;

import com.sysco.product_service.dto.CategoryDTO;
import com.sysco.product_service.entity.Category;
import com.sysco.product_service.exception.DuplicateResourceException;
import com.sysco.product_service.exception.InternalServerException;
import com.sysco.product_service.exception.InvalidInputException;
import com.sysco.product_service.exception.ResourceNotFoundException;
import com.sysco.product_service.mapper.CategoryMapper;
import com.sysco.product_service.repository.CategoryRepository;
import com.sysco.product_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private static final String CATEGORY_NOT_FOUND_MSG = "Category not found with ID: ";
    
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public ResponseEntity<CategoryDTO> createCategory(CategoryDTO categoryDTO) {
        try {
            log.info("Creating new category: {}", categoryDTO.getName());
            
            // Check if category already exists
            if (categoryRepository.existsByName(categoryDTO.getName())) {
                log.warn("Attempt to create category with duplicate name: {}", categoryDTO.getName());
                throw new DuplicateResourceException("Category with name '" + categoryDTO.getName() + "' already exists");
            }
            
            Category category = categoryMapper.toCreateCategory(categoryDTO);
            Category savedCategory = categoryRepository.save(category);
            CategoryDTO responseDTO = categoryMapper.toCreatedCategory(savedCategory);
            
            log.info("Category created successfully with ID: {}", savedCategory.getId());
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
            
        } catch (DuplicateResourceException e) {
            log.error("Duplicate category name exception: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while creating category: {}", e.getMessage(), e);
            throw new InternalServerException("An unexpected error occurred while creating the category");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        try {
            log.info("Fetching all categories");
            
            List<Category> categories = categoryRepository.findAll();
            List<CategoryDTO> categoryDTOs = categories.stream()
                    .map(categoryMapper::toCreatedCategory)
                    .toList();
            
            log.info("Retrieved {} categories", categoryDTOs.size());
            return new ResponseEntity<>(categoryDTOs, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Unexpected error while fetching all categories: {}", e.getMessage(), e);
            throw new InternalServerException("An unexpected error occurred while retrieving categories");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<CategoryDTO> getCategoryById(Long id) {
        try {
            log.info("Fetching category by ID: {}", id);
            
            if (id == null || id <= 0) {
                throw new InvalidInputException("Category ID must be a positive number");
            }
            
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MSG + id));
            
            CategoryDTO categoryDTO = categoryMapper.toCreatedCategory(category);
            
            log.info("Category retrieved successfully: {}", category.getName());
            return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
            
        } catch (ResourceNotFoundException | InvalidInputException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while fetching category with ID {}: {}", id, e.getMessage(), e);
            throw new InternalServerException("An unexpected error occurred while retrieving the category");
        }
    }

    @Override
    public ResponseEntity<CategoryDTO> updateCategory(Long id, CategoryDTO categoryDTO) {
        try {
            log.info("Updating category with ID: {}", id);
            
            if (id == null || id <= 0) {
                throw new InvalidInputException("Category ID must be a positive number");
            }
            
            Category existingCategory = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MSG + id));
            
            // Check if the new name conflicts with another category
            if (!existingCategory.getName().equals(categoryDTO.getName()) && 
                categoryRepository.existsByName(categoryDTO.getName())) {
                log.warn("Attempt to update category with duplicate name: {}", categoryDTO.getName());
                throw new DuplicateResourceException("Category with name '" + categoryDTO.getName() + "' already exists");
            }
            
            existingCategory.setName(categoryDTO.getName());
            existingCategory.setDescription(categoryDTO.getDescription());
            
            Category updatedCategory = categoryRepository.save(existingCategory);
            CategoryDTO responseDTO = categoryMapper.toCreatedCategory(updatedCategory);
            
            log.info("Category updated successfully: {}", updatedCategory.getName());
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
            
        } catch (ResourceNotFoundException | DuplicateResourceException | InvalidInputException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating category: {}", e.getMessage(), e);
            throw new InternalServerException("An unexpected error occurred while updating the category");
        }
    }

    @Override
    public ResponseEntity<Void> deleteCategory(Long id) {
        try {
            log.info("Deleting category with ID: {}", id);
            
            if (id == null || id <= 0) {
                throw new InvalidInputException("Category ID must be a positive number");
            }
            
            Category category = categoryRepository.findByIdWithProducts(id)
                    .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MSG + id));
            
            // Check if category has products
            if (category.getProducts() != null && !category.getProducts().isEmpty()) {
                log.warn("Attempt to delete category with existing products. Category ID: {}", id);
                throw new IllegalStateException("Cannot delete category with existing products. Please move or delete products first.");
            }
            
            categoryRepository.delete(category);
            
            log.info("Category deleted successfully with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            
        } catch (ResourceNotFoundException | InvalidInputException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting category: {}", e.getMessage(), e);
            throw new InternalServerException("An unexpected error occurred while deleting the category");
        }
    }
}