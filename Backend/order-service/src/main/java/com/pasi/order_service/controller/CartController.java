package com.pasi.order_service.controller;

import com.pasi.order_service.exception.CartAlreadyExistsException;
import com.pasi.order_service.exception.CartNotFoundException;
import com.pasi.order_service.exception.ItemNotFoundException;
import com.pasi.order_service.dto.AddItemToCartRequest;
import com.pasi.order_service.dto.CartResponse;
import com.pasi.order_service.dto.CreateCartRequest;
import com.pasi.order_service.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
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
        } catch (CartAlreadyExistsException e) {
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

    // Use customerId in paths because one customer maps to one cart
    @PostMapping("/{customerId}")
    public ResponseEntity<Map<String, Object>> addItemToCart(
            @PathVariable String customerId,
            @Valid @RequestBody AddItemToCartRequest request) {
        try {
            if (!isValidCustomerId(customerId)) {
                return createCartValidationErrorResponse("Customer ID", "must be between 1 and 50 characters");
            }

            // Resolve cart by customerId, service will throw CartNotFoundException if absent
            CartResponse existingCart = cartService.getCartByCustomerId(customerId);
            Long cartId = existingCart.getCartId();

            log.info("Adding item {} to cart: {} (customer: {})", request.getSkuCode(), cartId, customerId);
            CartResponse cartResponse = cartService.addItemToCart(customerId, request);
            return sendSuccessResponse(cartResponse, "Item added to cart successfully");
        } catch (CartNotFoundException e) {
            log.error("Cart not found for customer {}: {}", customerId, e.getMessage());
            return sendNotFoundResponse(e.getMessage());
        } catch (Exception e) {
            log.error("Error adding item to cart for customer {}: {}", customerId, e.getMessage());
            return sendInternalServerErrorResponse("Failed to add item to cart due to an unexpected error");
        }
    }

    /**
     * Get current customer's cart items
     * GET /api/v1/order/{cartId}
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<Map<String, Object>> getCart(@PathVariable String customerId) {
        try {
            if (!isValidCustomerId(customerId)) {
                return createCartValidationErrorResponse("Customer ID", "must be between 1 and 50 characters");
            }

            log.info("Retrieving cart for customer: {}", customerId);
            CartResponse cartResponse = cartService.getCartByCustomerId(customerId);
            return sendSuccessResponse(cartResponse, "Cart retrieved successfully");
        } catch (CartNotFoundException e) {
            log.error("Cart not found for customer {}: {}", customerId, e.getMessage());
            return sendNotFoundResponse(e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving cart for customer {}: {}", customerId, e.getMessage());
            return sendInternalServerErrorResponse("Failed to retrieve cart due to an unexpected error");
        }
    }

    /**
     * Remove item from cart
     * DELETE /api/v1/order/{customerId}/{skuCode}
     */
    @DeleteMapping("/{customerId}/{skuCode}")
    public ResponseEntity<Map<String, Object>> removeItemFromCart(
            @PathVariable String customerId,
            @PathVariable String skuCode) {
        try {
            if (!isValidCustomerId(customerId)) {
                return createCartValidationErrorResponse("Customer ID", "must be between 1 and 50 characters");
            }

            if (!isValidSkuCode(skuCode)) {
                return createCartValidationErrorResponse("SKU Code", "must be between 2 and 50 characters");
            }

            // Resolve cart and perform removal
            CartResponse existingCart = cartService.getCartByCustomerId(customerId);
            Long cartId = existingCart.getCartId();

            log.info("Removing item {} from cart: {} (customer: {})", skuCode, cartId, customerId);
            cartService.removeItemFromCart(customerId, skuCode);
            return sendNoContentResponse("Item removed from cart successfully");
        } catch (CartNotFoundException e) {
            log.error("Cart not found for customer {}: {}", customerId, e.getMessage());
            return sendNotFoundResponse(e.getMessage());
        } catch (ItemNotFoundException e) {
            log.error("Item not found for customer {}: {}", customerId, e.getMessage());
            return sendNotFoundResponse(e.getMessage());
        } catch (Exception e) {
            log.error("Error removing item {} for customer {}: {}", skuCode, customerId, e.getMessage());
            return sendInternalServerErrorResponse("Failed to remove item from cart due to an unexpected error");
        }
    }

    /**
     * Remove shopping cart for current customer
     * DELETE /api/v1/order/{customerId}
     */
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Map<String, Object>> removeCart(@PathVariable String customerId) {
        try {
            if (!isValidCustomerId(customerId)) {
                return createCartValidationErrorResponse("Customer ID", "must be between 1 and 50 characters");
            }

            CartResponse existingCart = cartService.getCartByCustomerId(customerId);
            Long cartId = existingCart.getCartId();

            log.info("Removing cart: {} (customer: {})", cartId, customerId);
            cartService.removeCart(customerId);
            return sendNoContentResponse("Cart removed successfully");
        } catch (CartNotFoundException e) {
            log.error("Cart not found for customer {}: {}", customerId, e.getMessage());
            return sendNotFoundResponse(e.getMessage());
        } catch (Exception e) {
            log.error("Error removing cart for customer {}: {}", customerId, e.getMessage());
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
        } catch (CartNotFoundException e) {
            log.error("Cart not found: {}", e.getMessage());
            return sendNotFoundResponse(e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving cart for customer {}: {}", customerId, e.getMessage());
            return sendInternalServerErrorResponse("Failed to retrieve cart due to an unexpected error");
        }
    }
}