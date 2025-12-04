package com.pasi.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {
    private String skuCode;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
}
