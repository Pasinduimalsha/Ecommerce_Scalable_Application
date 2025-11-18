package com.sysco.order_service.service.impl;

import com.sysco.order_service.dto.AddItemToCartRequest;
import com.sysco.order_service.dto.CartItemResponse;
import com.sysco.order_service.dto.CartResponse;
import com.sysco.order_service.dto.CreateCartRequest;
import com.sysco.order_service.entity.Cart;
import com.sysco.order_service.entity.CartItem;
import com.sysco.order_service.exception.CartNotFoundException;
import com.sysco.order_service.exception.ItemNotFoundException;
import com.sysco.order_service.exception.CartAlreadyExistsException;
import com.sysco.order_service.repository.CartRepository;
import com.sysco.order_service.repository.CartItemRepository;
import com.sysco.order_service.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

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

        return mapToCartResponse(savedCart);
    }

    @Override
    public CartResponse addItemToCart(Long cartId, AddItemToCartRequest request) {
        log.info("Adding item {} to cart {}", request.getSkuCode(), cartId);

        Cart cart = cartRepository.findByIdWithItems(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found with ID: " + cartId));

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndSkuCode(cartId, request.getSkuCode());

        if (existingItem.isPresent()) {
            // Update quantity if item already exists
            CartItem item = existingItem.get();
            item.updateQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
            log.info("Updated quantity for existing item {} in cart {}", request.getSkuCode(), cartId);
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
            log.info("Added new item {} to cart {}", request.getSkuCode(), cartId);
        }

        cart.calculateTotal();
        Cart savedCart = cartRepository.save(cart);

        return mapToCartResponse(savedCart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartById(Long cartId) {
        log.info("Retrieving cart with ID: {}", cartId);

        Cart cart = cartRepository.findByIdWithItems(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found with ID: " + cartId));

        return mapToCartResponse(cart);
    }

    @Override
    public void removeItemFromCart(Long cartId, String skuCode) {
        log.info("Removing item {} from cart {}", skuCode, cartId);

        Cart cart = cartRepository.findByIdWithItems(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found with ID: " + cartId));

        CartItem item = cartItemRepository.findByCartIdAndSkuCode(cartId, skuCode)
                .orElseThrow(() -> new ItemNotFoundException("Item not found in cart - SKU: " + skuCode));

        cart.removeItem(item);
        cartItemRepository.delete(item);
        
        cart.calculateTotal();
        cartRepository.save(cart);

        log.info("Item {} removed from cart {}", skuCode, cartId);
    }

    @Override
    public void removeCart(Long cartId) {
        log.info("Removing cart with ID: {}", cartId);

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found with ID: " + cartId));

        cartRepository.delete(cart);
        log.info("Cart {} removed successfully", cartId);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartByCustomerId(String customerId) {
        log.info("Retrieving cart for customer: {}", customerId);

        Cart cart = cartRepository.findByCustomerIdWithItems(customerId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for customer: " + customerId));

        return mapToCartResponse(cart);
    }

    private CartResponse mapToCartResponse(Cart cart) {
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

    private CartItemResponse mapToCartItemResponse(CartItem item) {
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