package com.pasi.order_service.mapper;

import com.pasi.order_service.dto.CartItemResponse;
import com.pasi.order_service.dto.CartResponse;
import com.pasi.order_service.entity.Cart;
import com.pasi.order_service.entity.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CartMapper {

    public CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        return CartResponse.builder()
                .cartId(cart.getId())
                .customerId(cart.getCustomerId())
                .items(itemResponses)
                .totalAmount(cart.getTotalAmount())
                .totalItems(cart.getTotalItems())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    public CartItemResponse mapToCartItemResponse(CartItem item) {
        return CartItemResponse.builder()
                .itemId(item.getId())
                .skuCode(item.getSkuCode())
                .productName(item.getProductName())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .subTotal(item.getSubTotal())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
