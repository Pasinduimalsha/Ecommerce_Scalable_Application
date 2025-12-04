package com.pasi.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequest {
    private String customerId;
    private String paymentMethod;
    private List<CartItemDto> items;
}
