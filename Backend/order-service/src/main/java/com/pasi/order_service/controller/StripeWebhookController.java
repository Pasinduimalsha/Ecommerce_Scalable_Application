package com.pasi.order_service.controller;

import com.pasi.order_service.entity.Order;
import com.pasi.order_service.repository.OrderRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/order")
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    private final OrderRepository orderRepository;

    @Value("${stripe.webhookSecret:}")
    private String webhookSecret;

    public StripeWebhookController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handle(@RequestHeader(value = "Stripe-Signature", required = false) String sigHeader,
                                         @RequestBody String payload) {
        log.info("Received webhook request, signature present: {}", sigHeader != null);
        
        Event event = null;

        try {
            if (webhookSecret != null && !webhookSecret.isEmpty() && sigHeader != null) {
                log.debug("Verifying webhook signature with secret");
                event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            } else {
                // No webhook secret configured — parse the payload without verification (use only for local testing)
                log.warn("Parsing webhook without signature verification - not recommended for production!");
                event = Event.GSON.fromJson(payload, Event.class);
            }
        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe signature: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            log.error("Failed to parse Stripe event: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload");
        }

        if (event == null) {
            log.error("Event is null after parsing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty event");
        }

        log.info("Successfully parsed Stripe event: type={}, id={}", event.getType(), event.getId());

        if ("checkout.session.completed".equals(event.getType())) {
            log.info("Processing checkout.session.completed event");
            // Deserialize session object
            Session session = null;
            try {
                session = event.getDataObjectDeserializer().getObject().map(obj -> (Session) obj).orElse(null);
            } catch (Exception ex) {
                log.error("Could not deserialize session object: {}", ex.getMessage(), ex);
            }

            if (session != null) {
                String clientRef = session.getClientReferenceId();
                String sessionId = session.getId();
                String paymentStatus = session.getPaymentStatus();
                
                log.info("Checkout session completed - sessionId={}, clientRef={}, paymentStatus={}", 
                        sessionId, clientRef, paymentStatus);
                
                if (clientRef != null) {
                    try {
                        Long orderId = Long.valueOf(clientRef);
                        Optional<Order> maybe = orderRepository.findById(orderId);
                        if (maybe.isPresent()) {
                            Order order = maybe.get();
                            String previousStatus = order.getStatus();
                            order.setStatus("PAID");
                            orderRepository.save(order);
                            log.info("Order {} status updated from {} to PAID", orderId, previousStatus);
                        } else {
                            log.error("Order not found for id {}", orderId);
                        }
                    } catch (NumberFormatException nfe) {
                        log.error("Invalid client reference id format: {}", clientRef, nfe);
                    }
                } else {
                    log.error("No client reference id available on session {}", sessionId);
                }
            } else {
                log.error("Session object is null after deserialization");
            }
        } else {
            log.debug("Ignoring event type: {}", event.getType());
        }

        return ResponseEntity.ok("received");
    }
}
