package com.swpteam.smokingcessation.apis.transaction.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.net.Webhook;
import com.swpteam.smokingcessation.apis.transaction.service.StripeWebhookService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook/stripe")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StripeWebhookController {
//    @Value("${stripe.webhook.secret}")
//    String webhookSecret;
//
//    StripeWebhookService stripeWebhookService;
//
//    @PostMapping
//    public ResponseEntity<String> handleStripeWebhook(
//            @RequestBody String payload,
//            @RequestHeader("Stripe-Signature") String sigHeader) {
//
//        try {
//            // Verify webhook signature
//            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
//
//            // Deserialize the event data object
//            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
//            if (dataObjectDeserializer.getObject().isEmpty()) {
//                return new ResponseEntity<>("Invalid event data", HttpStatus.BAD_REQUEST);
//            }
//
//            // Handle different event types
//            switch (event.getType()) {
//                case "customer.subscription.created":
//                    stripeWebhookService.handleSubscriptionCreated(dataObjectDeserializer);
//                    break;
//                case "customer.subscription.updated":
//                    stripeWebhookService.handleSubscriptionUpdated(dataObjectDeserializer);
//                    break;
//                case "customer.subscription.deleted":
//                    stripeWebhookService.handleSubscriptionDeleted(dataObjectDeserializer);
//                    break;
//                default:
//                    // Log unhandled event types
//                    System.out.println("Unhandled event type: " + event.getType());
//            }
//
//            return new ResponseEntity<>("Webhook processed successfully", HttpStatus.OK);
//
//        } catch (SignatureVerificationException e) {
//            // Invalid signature
//            return new ResponseEntity<>("Invalid signature", HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            // Other errors
//            return new ResponseEntity<>("Webhook processing failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
