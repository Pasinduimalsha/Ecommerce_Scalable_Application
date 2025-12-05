package com.pasi.order_service.repository;

import com.pasi.order_service.entity.CartItem;
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
    
    @Query("SELECT ci FROM CartItem ci JOIN ci.cart c WHERE c.customerId = :customerId AND ci.skuCode = :skuCode")
    Optional<CartItem> findItemByCustomerIdAndSkuCode(@Param("customerId") String customerId, @Param("skuCode") String skuCode);

    @Query("SELECT ci FROM CartItem ci JOIN ci.cart c WHERE c.customerId = :customerId AND ci.skuCode IN :skuCodes")
            List<CartItem> findItemsByCustomerIdAndSkuCodes(@Param("customerId") String customerId,
            @Param("skuCodes") List<String> skuCodes);

}