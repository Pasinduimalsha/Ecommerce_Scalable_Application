package com.pasi.order_service.service;

import com.pasi.order_service.dto.CheckoutRequest;
import com.pasi.order_service.dto.CheckoutResponse;

public interface CheckoutService {
    CheckoutResponse checkout(CheckoutRequest request);
}
