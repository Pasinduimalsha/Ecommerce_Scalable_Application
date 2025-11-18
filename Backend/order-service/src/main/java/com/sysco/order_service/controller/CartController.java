package com.sysco.order_service.controller;

import com.sysco.order_service.dto.AddItemToCartRequest;
import com.sysco.order_service.dto.CartResponse;
import com.sysco.order_service.dto.CreateCartRequest;
import com.sysco.order_service.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CartController extends OrderAbstractController {

    private final CartService cartService;

    /**
     * Create a new cart for a registered customer
     * POST /api/v1/order
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCart(@Valid @RequestBody CreateCartRequest request) {
        try {
            log.info("Creating cart for customer: {}", request.getCustomerId());
            CartResponse cartResponse = cartService.createCart(request);
            return sendCreatedResponse(cartResponse, "Cart created successfully for customer: " + request.getCustomerId());
        } catch (com.sysco.order_service.exception.CartAlreadyExistsException e) {
            log.error("Cart already exists for customer {}: {}", request.getCustomerId(), e.getMessage());
            return sendConflictResponse(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating cart for customer {}: {}", request.getCustomerId(), e.getMessage());
            return sendInternalServerErrorResponse("Failed to create cart due to an unexpected error");
        }
    }

    /**
     * Add new item to cart
     * POST /api/v1/order/{cartId}
     */
    @PostMapping("/{cartId}")
    public ResponseEntity<Map<String, Object>> addItemToCart(
            @PathVariable Long cartId,
            @Valid @RequestBody AddItemToCartRequest request) {
        try {
            if (!isValidCartId(cartId)) {
                return createCartValidationErrorResponse("Cart ID", "must be a positive number");
            }
            
            log.info("Adding item {} to cart: {}", request.getSkuCode(), cartId);
            CartResponse cartResponse = cartService.addItemToCart(cartId, request);
            return sendSuccessResponse(cartResponse, "Item added to cart successfully");
        } catch (com.sysco.order_service.exception.CartNotFoundException e) {
            log.error("Cart not found: {}", e.getMessage());
            return sendNotFoundResponse(e.getMessage());
        } catch (Exception e) {
            log.error("Error adding item to cart {}: {}", cartId, e.getMessage());
            return sendInternalServerErrorResponse("Failed to add item to cart due to an unexpected error");
        }
    }

    /**
     * Get current customer's cart items
     * GET /api/v1/order/{cartId}
     */
    @GetMapping("/{cartId}")
    public ResponseEntity<Map<String, Object>> getCart(@PathVariable Long cartId) {
        try {
            if (!isValidCartId(cartId)) {
                return createCartValidationErrorResponse("Cart ID", "must be a positive number");
            }
            
            log.info("Retrieving cart: {}", cartId);
            CartResponse cartResponse = cartService.getCartById(cartId);
            return sendSuccessResponse(cartResponse, "Cart retrieved successfully");
        } catch (com.sysco.order_service.exception.CartNotFoundException e) {
            log.error("Cart not found: {}", e.getMessage());
            return sendNotFoundResponse(e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving cart {}: {}", cartId, e.getMessage());
            return sendInternalServerErrorResponse("Failed to retrieve cart due to an unexpected error");
        }
    }

    /**
     * Remove item from cart
     * DELETE /api/v1/order/{cartId}/{skuCode}
     */
    @DeleteMapping("/{cartId}/{skuCode}")
    public ResponseEntity<Map<String, Object>> removeItemFromCart(
            @PathVariable Long cartId,
            @PathVariable String skuCode) {
        try {
            if (!isValidCartId(cartId)) {
                return createCartValidationErrorResponse("Cart ID", "must be a positive number");
            }
            
            if (!isValidSkuCode(skuCode)) {
                return createCartValidationErrorResponse("SKU Code", "must be between 2 and 50 characters");
            }
            
            log.info("Removing item {} from cart: {}", skuCode, cartId);
            cartService.removeItemFromCart(cartId, skuCode);
            return sendNoContentResponse("Item removed from cart successfully");
        } catch (com.sysco.order_service.exception.CartNotFoundException e) {
            log.error("Cart not found: {}", e.getMessage());
            return sendNotFoundResponse(e.getMessage());
        } catch (com.sysco.order_service.exception.ItemNotFoundException e) {
            log.error("Item not found: {}", e.getMessage());
            return sendNotFoundResponse(e.getMessage());
        } catch (Exception e) {
            log.error("Error removing item {} from cart {}: {}", skuCode, cartId, e.getMessage());
            return sendInternalServerErrorResponse("Failed to remove item from cart due to an unexpected error");
        }
    }

    /**
     * Remove shopping cart for current customer
     * DELETE /api/v1/order/{cartId}
     */
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Map<String, Object>> removeCart(@PathVariable Long cartId) {
        try {
            if (!isValidCartId(cartId)) {
                return createCartValidationErrorResponse("Cart ID", "must be a positive number");
            }
            
            log.info("Removing cart: {}", cartId);
            cartService.removeCart(cartId);
            return sendNoContentResponse("Cart removed successfully");
        } catch (com.sysco.order_service.exception.CartNotFoundException e) {
            log.error("Cart not found: {}", e.getMessage());
            return sendNotFoundResponse(e.getMessage());
        } catch (Exception e) {
            log.error("Error removing cart {}: {}", cartId, e.getMessage());
            return sendInternalServerErrorResponse("Failed to remove cart due to an unexpected error");
        }
    }

    /**
     * Additional endpoint to get cart by customer ID
     * GET /api/v1/order/customer/{customerId}
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> getCartByCustomerId(@PathVariable String customerId) {
        try {
            if (!isValidCustomerId(customerId)) {
                return createCartValidationErrorResponse("Customer ID", "must be between 1 and 50 characters");
            }
            
            log.info("Retrieving cart for customer: {}", customerId);
            CartResponse cartResponse = cartService.getCartByCustomerId(customerId);
            return sendSuccessResponse(cartResponse, "Cart retrieved successfully for customer: " + customerId);
        } catch (com.sysco.order_service.exception.CartNotFoundException e) {
            log.error("Cart not found: {}", e.getMessage());
            return sendNotFoundResponse(e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving cart for customer {}: {}", customerId, e.getMessage());
            return sendInternalServerErrorResponse("Failed to retrieve cart due to an unexpected error");
        }
    }
}