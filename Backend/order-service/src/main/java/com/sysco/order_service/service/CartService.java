package com.sysco.order_service.service;

import com.sysco.order_service.dto.AddItemToCartRequest;
import com.sysco.order_service.dto.CartResponse;
import com.sysco.order_service.dto.CreateCartRequest;

public interface CartService {
    
    CartResponse createCart(CreateCartRequest request);
    
    CartResponse addItemToCart(Long cartId, AddItemToCartRequest request);
    
    CartResponse getCartById(Long cartId);
    
    void removeItemFromCart(Long cartId, String skuCode);
    
    void removeCart(Long cartId);
    
    CartResponse getCartByCustomerId(String customerId);
}