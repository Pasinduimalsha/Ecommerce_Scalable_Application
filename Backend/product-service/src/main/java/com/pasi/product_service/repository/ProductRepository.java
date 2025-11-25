package com.pasi.product_service.repository;

import com.pasi.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySku(String sku);

//     Optional<Product> findBySku(String sku);

    List<Product> findByStatus(Product.Status status);
    
//     @Query("SELECT p FROM Product p WHERE p.status = :status")
//     List<Product> findProductsByStatus(@Param("status") Product.Status status);
    
    // New search methods - updated for Category entity relationship
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR " +
           "LOWER(p.category.name) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :searchValue, '%'))")
    List<Product> searchProducts(@Param("searchValue") String searchValue);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR " +
           "LOWER(p.category.name) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :searchValue, '%'))) AND " +
           "p.status = :status")
    List<Product> searchProductsByStatus(@Param("searchValue") String searchValue, 
                                       @Param("status") Product.Status status);

//     // Find products by category name
//     @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category c WHERE LOWER(c.name) = LOWER(:categoryName)")
//     List<Product> findByCategoryName(@Param("categoryName") String categoryName);
    
    // Find approved products by category name
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category c WHERE LOWER(c.name) = LOWER(:categoryName) AND p.status = 'APPROVED'")
    List<Product> findApprovedProductsByCategoryName(@Param("categoryName") String categoryName);

    // Find all approved products
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.status = 'APPROVED'")
    List<Product> findAllApprovedProducts();

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);

//     @Query("SELECT p FROM Product p WHERE p.name LIKE %:name%")
//     List<Product> findByNameContaining(@Param("name") String name);

//     @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 AND p.status = 'APPROVED'")
//     List<Product> findAvailableProducts();

//     @Query("SELECT p FROM Product p WHERE p.stockQuantity <= :threshold")
//     List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

//     Optional<Product> findByIdAndStatus(Long id, Product.Status status);
}