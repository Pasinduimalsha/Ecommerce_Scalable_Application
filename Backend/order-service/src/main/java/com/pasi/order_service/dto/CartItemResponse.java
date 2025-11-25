package com.pasi.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private Long itemId;
    private String skuCode;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subTotal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}