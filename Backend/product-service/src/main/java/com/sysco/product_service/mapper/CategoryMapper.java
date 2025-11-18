package com.sysco.product_service.mapper;

import com.sysco.product_service.dto.CategoryDTO;
import com.sysco.product_service.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    /**
     * Converts Category entity to CategoryDTO
     * @param category the Category entity to convert
     * @return CategoryDTO representation of the category
     */
    public CategoryDTO toCreatedCategory(Category category) {
        if (category == null) {
            return null;
        }
        
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    /**
     * Converts CategoryDTO to Category entity
     * @param categoryDTO the CategoryDTO to convert
     * @return Category entity representation of the DTO
     */
    public Category toCreateCategory(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            return null;
        }
        
        return Category.builder()
                .id(categoryDTO.getId())
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .createdAt(categoryDTO.getCreatedAt())
                .updatedAt(categoryDTO.getUpdatedAt())
                .build();
    }

    /**
     * Updates an existing Category entity with data from CategoryDTO
     * @param existingCategory the existing Category entity to update
     * @param categoryDTO the CategoryDTO containing new data
     * @return the updated Category entity
     */
    public Category updateCategoryFromDTO(Category existingCategory, CategoryDTO categoryDTO) {
        if (existingCategory == null || categoryDTO == null) {
            return existingCategory;
        }
        
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());
        // Note: createdAt and updatedAt are managed by JPA lifecycle methods
        
        return existingCategory;
    }
}