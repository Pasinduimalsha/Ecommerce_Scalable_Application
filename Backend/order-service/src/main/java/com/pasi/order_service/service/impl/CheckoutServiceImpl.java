package com.pasi.order_service.service.impl;

import com.pasi.order_service.clients.InventoryClient;
import com.pasi.order_service.dto.CartItemDto;
import com.pasi.order_service.dto.CheckoutRequest;
import com.pasi.order_service.dto.CheckoutResponse;
import com.pasi.order_service.entity.Cart;
import com.pasi.order_service.entity.CartItem;
import com.pasi.order_service.entity.Order;
import com.pasi.order_service.entity.OrderItem;
import com.pasi.order_service.repository.CartItemRepository;
import com.pasi.order_service.repository.CartRepository;
import com.pasi.order_service.repository.OrderRepository;
import com.pasi.order_service.service.CheckoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pasi.order_service.exception.ProductNotFoundException;
import com.pasi.order_service.service.StripeService;
import com.pasi.order_service.dto.PaymentRequest;
import com.pasi.order_service.dto.StripeResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private static final Logger log = LoggerFactory.getLogger(CheckoutServiceImpl.class);

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final InventoryClient inventoryClient;
    private final StripeService stripeService;

    public CheckoutServiceImpl(OrderRepository orderRepository, CartRepository cartRepository, CartItemRepository cartItemRepository, InventoryClient inventoryClient, StripeService stripeService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.inventoryClient = inventoryClient;
        this.stripeService = stripeService;
    }

    @Override
    public CheckoutResponse checkout(CheckoutRequest request) {
        List<CartItemDto> items = request.getItems();
        if (items.isEmpty()) {
            return CheckoutResponse.builder()
                    .status("FAILED")
                    .message("Cart is empty")
                    .build();
        }

        Optional<Cart> checkoutCart = cartRepository.findCartByCustomerId(request.getCustomerId());
        if (checkoutCart.isEmpty()) {
            return CheckoutResponse.builder()
                    .status("FAILED")
                    .message("Cart not found for customerId: " + request.getCustomerId())
                    .build();
        }

        //Construct SKU code list to check product availability
        List<String> skuCodes = request.getItems()
                               .stream()
                               .map(CartItemDto::getSkuCode)
                               .toList();
        System.out.println("skuCodes"+ skuCodes);
        List<CartItem> cartItems = cartItemRepository
                .findItemsByCustomerIdAndSkuCodes(request.getCustomerId(), skuCodes);

        // validate
        List<String> cartSkuCodes = cartItems.stream()
                                            .map(CartItem::getSkuCode)
                                            .toList();

        List<String> missing = skuCodes.stream()
                                    .filter(code -> !cartSkuCodes.contains(code))
                                    .toList();
        // If there are missing items, throw an exception
        if (!missing.isEmpty()) {
            throw new ProductNotFoundException("Missing cart items: " + missing);
        }

        // Reserve stocks
        BigDecimal total = calculateTotal(items);

        // Reserve stock
        boolean reserved = reserveStock(items);
        if (!reserved) {
            return CheckoutResponse.builder()
                    .orderId(null)
                    .status("FAILED")
                    .message("Insufficient stock for one or more items")
                    .total(total)
                    .build();
        }
        
         // Proceed with create order
        Order order = buildOrder(request, total);
        order.setStatus("CREATED");
        Order perOrder = persistOrder(order, items);

        // Process payment and return response
        return stripeService.processPayment(perOrder, total);

        // Proceed with create order
        // Order order = buildOrder(request, total);
        // order.setStatus("PENDING_PAYMENT");
        // Order perOrder = persistOrder(order, items);

        // return CheckoutResponse.builder()
        //         .orderId(perOrder.getId())
        //         .status("PENDING_PAYMENT")
        //         .message("Order created successfully, pending payment")
        //         .total(total)
        //         .build();
      
        // // Create payment session using Stripe (returns session info or failure)
        // PaymentRequest paymentRequest = PaymentRequest.builder()
        //     .name("Order-" + perOrder.getId())
        //     // Stripe expects amount in smallest currency unit (cents)
        //     .amount(total.multiply(java.math.BigDecimal.valueOf(100)).longValue())
        //     .quantity(1L)
        //     .currency("USD")
        //     .clientReferenceId(String.valueOf(perOrder.getId()))
        //     .build();

        // StripeResponse stripeResponse = stripeService.checkoutProducts(paymentRequest);

        // // Note: current StripeServiceImpl returns status "PAID" when session created.
        // if (stripeResponse != null && "PENDING_PAYMENT".equals(stripeResponse.getStatus())) {
        //     perOrder.setStatus("PENDING_PAYMENT");
        //     orderRepository.save(perOrder);

        //     log.info("Order {} created and payment session opened, sessionId={}", perOrder.getId(), stripeResponse.getSessionId());

        //     return CheckoutResponse.builder()
        //         .orderId(perOrder.getId())
        //         .status("PENDING_PAYMENT")
        //         .message("Payment session created: " + stripeResponse.getSessionUrl())
        //         .total(total)
        //         .build();
        // }

        // Payment session creation failed — release reserved stock and mark order
        // inventoryClient.releaseStock(items);
        // perOrder.setStatus("PAYMENT_FAILED");
        // orderRepository.save(perOrder);

        // return CheckoutResponse.builder()
        //     .orderId(perOrder.getId())
        //     .status("FAILED")
        //     .message("Payment failed: " + (stripeResponse != null ? stripeResponse.getMessage() : "Unknown"))
        //     .total(total)
        //     .build();
      
    }

    private boolean reserveStock(List<CartItemDto> items) {
        return inventoryClient.reserveStock(items);
    }

    private BigDecimal calculateTotal(List<CartItemDto> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItemDto it : items) {
            BigDecimal unit = it.getUnitPrice() != null ? it.getUnitPrice() : BigDecimal.ZERO;
            int qty = it.getQuantity() != null ? it.getQuantity() : 0;
            BigDecimal sub = unit.multiply(BigDecimal.valueOf(qty));
            total = total.add(sub);
        }
        return total;
    }

    private Order buildOrder(CheckoutRequest request, BigDecimal total) {
        return Order.builder()
                .customerId(request.getCustomerId())
                .status("CREATED")
                .total(total)
                .build();
    }

    private Order persistOrder(Order order, List<CartItemDto> items) {
        for (CartItemDto it : items) {
            OrderItem orderItems = OrderItem.builder()
                    .skuCode(it.getSkuCode())
                    .productName(it.getProductName())
                    .unitPrice(it.getUnitPrice())
                    .quantity(it.getQuantity())
                    .subTotal(it.getUnitPrice() != null && it.getQuantity() != null ? it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity())) : BigDecimal.ZERO)
                    .build();
            order.addItem(orderItems);
        }
        log.info("Order persisted successfully with status PENDING_PAYMENT");
        return orderRepository.save(order);
    }
}
