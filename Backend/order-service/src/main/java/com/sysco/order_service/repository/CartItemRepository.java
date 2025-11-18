package com.sysco.order_service.repository;

import com.sysco.order_service.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> findByCartId(Long cartId);
    
    Optional<CartItem> findByCartIdAndSkuCode(Long cartId, String skuCode);
    
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.skuCode = :skuCode")
    Optional<CartItem> findByCartIdAndSkuCodeWithCart(@Param("cartId") Long cartId, @Param("skuCode") String skuCode);
    
    void deleteByCartIdAndSkuCode(Long cartId, String skuCode);
    
    void deleteByCartId(Long cartId);
}