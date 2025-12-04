package com.pasi.order_service.clients;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentClient {

    public PaymentResult processPayment(String customerId, BigDecimal amount, String method) {
        // TODO: integrate with payment gateway; use idempotency keys in real integration
        PaymentResult r = new PaymentResult();
        r.setSuccess(true);
        r.setTransactionId("tx_" + System.currentTimeMillis());
        r.setMessage("Simulated success");
        return r;
    }

    public static class PaymentResult {
        private boolean success;
        private String transactionId;
        private String message;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
