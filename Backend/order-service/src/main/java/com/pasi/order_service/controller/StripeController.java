package com.pasi.order_service.controller;

import com.pasi.order_service.dto.PaymentRequest;
import com.pasi.order_service.dto.StripeResponse;
import com.pasi.order_service.service.StripeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class StripeController {

    private static StripeService stripeService;

    @PostMapping
    public ResponseEntity<StripeResponse> checkoutProducts(@RequestBody PaymentRequest paymentRequest) {
        StripeResponse stripeResponse = stripeService.checkoutProducts(paymentRequest);
        if ("PAID".equals(stripeResponse.getStatus())) {
            return ResponseEntity.ok(stripeResponse);
        }
        return ResponseEntity.badRequest().body(stripeResponse);
    }

    @GetMapping("/success")
    public ResponseEntity<String> success() {
        // Called by Stripe after successful payment
        return ResponseEntity.ok("Payment successful. Thank you!");
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> cancel() {
        // Called by Stripe when user cancels checkout
        return ResponseEntity.ok("Payment was cancelled.");
    }
}
