package com.pasi.order_service.service;

import com.pasi.order_service.dto.PaymentRequest;
import com.pasi.order_service.dto.StripeResponse;
import com.pasi.order_service.dto.CheckoutResponse;
import com.pasi.order_service.entity.Order;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public interface StripeService {
    StripeResponse checkoutProducts(PaymentRequest productRequest);
    
    /**
     * Create a payment session for the given order and handle payment workflow.
     * This method orchestrates the complete payment process including:
     * - Creating a Stripe checkout session
     * - Updating order status based on payment session creation
     * - Releasing reserved stock if payment session creation fails
     * 
     * @param order the order to process payment for
     * @param total the total amount to charge
     * @return CheckoutResponse with payment session details or failure information
     */
    CheckoutResponse processPayment(Order order, BigDecimal total);
}
