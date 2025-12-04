package com.pasi.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    // amount in cents (or smallest currency unit) - Stripe expects integer
    private String name;
    private Long quantity;
    private Long amount;
    private String currency;
    // optional client reference (e.g. order id) to correlate session with order
    private String clientReferenceId;
}
