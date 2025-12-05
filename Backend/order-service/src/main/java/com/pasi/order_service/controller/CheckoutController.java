package com.pasi.order_service.controller;

import com.pasi.order_service.dto.CheckoutRequest;
import com.pasi.order_service.dto.CheckoutResponse;
import com.pasi.order_service.dto.PaymentRequest;
import com.pasi.order_service.dto.StripeResponse;
import com.pasi.order_service.service.CheckoutService;
import com.pasi.order_service.service.StripeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final StripeService stripeService;

    public CheckoutController(CheckoutService checkoutService, StripeService stripeService) {
        this.checkoutService = checkoutService;
        this.stripeService = stripeService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody CheckoutRequest request) {
        CheckoutResponse resp = checkoutService.checkout(request);
        if ("PENDING_PAYMENT".equals(resp.getStatus())) {
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.badRequest().body(resp);
    }
}
