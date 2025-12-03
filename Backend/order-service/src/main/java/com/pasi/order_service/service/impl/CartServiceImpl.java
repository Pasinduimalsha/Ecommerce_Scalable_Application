package com.pasi.order_service.service.impl;

import com.pasi.order_service.exception.CartAlreadyExistsException;
import com.pasi.order_service.repository.CartItemRepository;
import com.pasi.order_service.repository.CartRepository;
import com.pasi.order_service.dto.AddItemToCartRequest;
import com.pasi.order_service.dto.CartResponse;
import com.pasi.order_service.dto.CreateCartRequest;
import com.pasi.order_service.entity.Cart;
import com.pasi.order_service.entity.CartItem;
import com.pasi.order_service.exception.CartNotFoundException;
import com.pasi.order_service.exception.ItemNotFoundException;
import com.pasi.order_service.service.CartService;
import com.pasi.order_service.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;

    @Override
    public CartResponse createCart(CreateCartRequest request) {
        log.info("Creating cart for customer: {}", request.getCustomerId());
        
        // Check if cart already exists for the customer
        if (cartRepository.existsByCustomerId(request.getCustomerId())) {
            throw new CartAlreadyExistsException("Cart already exists for customer: " + request.getCustomerId());
        }

        Cart cart = Cart.builder()
                .customerId(request.getCustomerId())
                .totalAmount(BigDecimal.ZERO)
                .build();

        Cart savedCart = cartRepository.save(cart);
        log.info("Cart created successfully with ID: {}", savedCart.getId());

        return cartMapper.mapToCartResponse(savedCart);
    }

    @Override
    public CartResponse addItemToCart(String customerId, AddItemToCartRequest request) {
        log.info("Adding item {} to cart by customer {}", request.getSkuCode(), customerId);

        Cart cart = cartRepository.findCartByCustomerId(customerId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found with customerId: " + customerId));

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findItemByCustomerIdAndSkuCode(customerId, request.getSkuCode());

        if (existingItem.isPresent()) {
            // Update quantity if item already exists
            CartItem item = existingItem.get();
            item.updateQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
            log.info("Updated quantity for existing item {} in cart by customerId {}", request.getSkuCode(), customerId);
        } else {
            // Add new item to cart
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .skuCode(request.getSkuCode())
                    .productName(request.getProductName())
                    .unitPrice(request.getUnitPrice())
                    .quantity(request.getQuantity())
                    .build();

            cart.addItem(newItem);
            cartItemRepository.save(newItem);
            log.info("Added new item {} to cart by customer {}", request.getSkuCode(), customerId);
        }

        cart.calculateTotal();
        Cart savedCart = cartRepository.save(cart);

        return cartMapper.mapToCartResponse(savedCart);
    }

    

    @Override
    public void removeItemFromCart(String customerId, String skuCode) {
    log.info("Removing item {} from cart for customer {}", skuCode, customerId);

    Cart cart = cartRepository.findByCustomerIdWithItems(customerId)
        .orElseThrow(() -> new CartNotFoundException("Cart not found for customer: " + customerId));

    CartItem item = cartItemRepository.findItemByCustomerIdAndSkuCode(customerId, skuCode)
        .orElseThrow(() -> new ItemNotFoundException("Item not found in cart - SKU: " + skuCode));

    cart.removeItem(item);
    cartItemRepository.delete(item);

    cart.calculateTotal();
    cartRepository.save(cart);

    log.info("Item {} removed from cart for customer {}", skuCode, customerId);
    }

    @Override
    public void removeCart(String customerId) {
        log.info("Removing cart for customer: {}", customerId);

        Cart cart = cartRepository.findCartByCustomerId(customerId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for customer: " + customerId));

        cartRepository.delete(cart);
        log.info("Cart for customer {} removed successfully", customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartByCustomerId(String customerId) {
        log.info("Retrieving cart for customer: {}", customerId);

        Cart cart = cartRepository.findByCustomerIdWithItems(customerId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for customer: " + customerId));

        return cartMapper.mapToCartResponse(cart);
    }
}