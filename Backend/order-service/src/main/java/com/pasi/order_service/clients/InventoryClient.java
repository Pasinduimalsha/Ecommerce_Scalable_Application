package com.pasi.order_service.clients;

import com.pasi.order_service.dto.CartItemDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Lightweight stub for inventory interactions. Replace with real HTTP/gRPC client.
 */
@Component
public class InventoryClient {

    public boolean reserveStock(List<CartItemDto> items) {
        // TODO: call inventory service; return true if reservation succeeded
        return true;
    }

    public void releaseStock(List<CartItemDto> items) {
        // TODO: call inventory service to release reservation
    }
}
