package com.pasi.order_service.service;

import com.pasi.order_service.dto.CartResponse;
import com.pasi.order_service.dto.AddItemToCartRequest;
import com.pasi.order_service.dto.CreateCartRequest;

public interface CartService {
    
    CartResponse createCart(CreateCartRequest request);
    
    CartResponse addItemToCart(String customerId, AddItemToCartRequest request);
    
    CartResponse getCartByCustomerId(String customerId);
    
    void removeItemFromCart(String customerId, String skuCode);
    
    void removeCart(String customerId);
}