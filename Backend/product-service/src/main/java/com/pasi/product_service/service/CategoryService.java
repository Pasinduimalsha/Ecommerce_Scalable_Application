package com.pasi.product_service.service;

import com.pasi.product_service.dto.CategoryDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoryService {
    
    ResponseEntity<CategoryDTO> createCategory(CategoryDTO categoryDTO);
    
    ResponseEntity<List<CategoryDTO>> getAllCategories();
    
    ResponseEntity<CategoryDTO> getCategoryById(Long id);
    
    ResponseEntity<CategoryDTO> updateCategory(Long id, CategoryDTO categoryDTO);
    
    ResponseEntity<Void> deleteCategory(Long id);
}