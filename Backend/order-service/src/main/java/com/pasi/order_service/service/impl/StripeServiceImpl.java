package com.pasi.order_service.service.impl;

import com.pasi.order_service.dto.PaymentRequest;
import com.pasi.order_service.dto.StripeResponse;
import com.pasi.order_service.dto.CheckoutResponse;
import com.pasi.order_service.entity.Order;
import com.pasi.order_service.repository.OrderRepository;
import com.pasi.order_service.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeServiceImpl implements StripeService {

    private static final Logger log = LoggerFactory.getLogger(StripeServiceImpl.class);

    @Value("${stripe.secretKey}")
    private String secretKey;
    
    private final OrderRepository orderRepository;

    public StripeServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public StripeResponse checkoutProducts(PaymentRequest paymentRequest) {
        // Stripe.apiKey is set by StripeConfig bean; set again defensively
        com.stripe.Stripe.apiKey = secretKey;

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(paymentRequest.getName())
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(paymentRequest.getCurrency() != null ? paymentRequest.getCurrency() : "USD")
                        .setUnitAmount(paymentRequest.getAmount())
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams
                        .LineItem.builder()
                        .setQuantity(paymentRequest.getQuantity())
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8083/api/v1/payment/success")
                .setCancelUrl("http://localhost:8083/api/v1/payment/cancel")
                .addLineItem(lineItem);

        if (paymentRequest.getClientReferenceId() != null) {
            paramsBuilder.setClientReferenceId(paymentRequest.getClientReferenceId());
        }

        SessionCreateParams params = paramsBuilder.build();

        try {
            Session session = Session.create(params);
            String sessionId = session.getId();
            String fallbackUrl = "https://checkout.stripe.com/c/pay/" + sessionId+"#"+"fidnandhYHdWcXxpYCc%2FJ2FgY2RwaXEnKSdkdWxOYHwnPyd1blpxYHZxWjA0VmRDdGcxUGtfQzN9cXQ3NHNHUlJ2PFN3YDFINVBUcWxIYldsYFNDdmg3RzZsbjRsMXZXPGBOTFJyPGNiXVdJbDdzf3JIaVRtZFFEPHI8PVVmTX9iPTE1NTV%2FYV1qb0ZUaScpJ2N3amhWYHdzYHcnP3F3cGApJ2dkZm5id2pwa2FGamlqdyc%2FJyZjY2NjY2MnKSdpZHxqcHFRfHVgJz8ndmxrYmlgWmxxYGgnKSdga2RnaWBVaWRmYG1qaWFgd3YnP3F3cGB4JSUl";
            return StripeResponse.builder()
                    .status("PENDING_PAYMENT")
                    .message("Payment session created")
                    .sessionId(sessionId)
                    .sessionUrl(fallbackUrl)
                    .build();
        } catch (StripeException e) {
            return StripeResponse.builder()
                    .status("FAILED")
                    .message("Stripe error: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public CheckoutResponse processPayment(Order order, BigDecimal total) {
        // Create payment session using Stripe
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .name("Order-" + order.getId())
                // Stripe expects amount in smallest currency unit (cents)
                .amount(total.multiply(BigDecimal.valueOf(100)).longValue())
                .quantity(1L)
                .currency("USD")
                .clientReferenceId(String.valueOf(order.getId()))
                .build();

        StripeResponse stripeResponse = checkoutProducts(paymentRequest);

        // Check if payment session was created successfully
        if (stripeResponse != null && "PENDING_PAYMENT".equals(stripeResponse.getStatus())) {
            order.setStatus("PENDING_PAYMENT");
            orderRepository.save(order);

            log.info("Order {} created and payment session opened, sessionId={}", order.getId(), stripeResponse.getSessionId());

            return CheckoutResponse.builder()
                    .orderId(order.getId())
                    .status("PENDING_PAYMENT")
                    .message("Payment session created: " + stripeResponse.getSessionUrl())
                    .total(total)
                    .build();
        }

        // Payment session creation failed — mark order as failed
        order.setStatus("PAYMENT_FAILED");
        orderRepository.save(order);

        log.warn("Payment session creation failed for order {}: {}", order.getId(), 
                stripeResponse != null ? stripeResponse.getMessage() : "Unknown error");

        return CheckoutResponse.builder()
                .orderId(order.getId())
                .status("FAILED")
                .message("Payment failed: " + (stripeResponse != null ? stripeResponse.getMessage() : "Unknown"))
                .total(total)
                .build();
    }
}
