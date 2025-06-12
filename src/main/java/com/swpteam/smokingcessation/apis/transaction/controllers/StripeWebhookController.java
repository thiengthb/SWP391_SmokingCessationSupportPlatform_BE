package com.swpteam.smokingcessation.apis.transaction.controllers;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.swpteam.smokingcessation.apis.transaction.services.StripeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Slf4j
@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StripeWebhookController {

    @NonFinal
    @Value("${stripe.webhook.secret}")
    protected String endpointSecret;

    StripeService stripeService;

    @PostMapping("/stripe")
    String handleStripeEvent(HttpServletRequest request, @RequestHeader("Stripe-Signature") String sigHeader) {
        String payload = "";
        try (Scanner s = new Scanner(request.getInputStream(), StandardCharsets.UTF_8)) {
            payload = s.useDelimiter("\\A").hasNext() ? s.next() : "";
        } catch (IOException e) {
            return "Error reading payload";
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return "Webhook signature verification failed.";
        }

        switch (event.getType()) {
            case "checkout.session.completed":
                stripeService.handleCheckoutSessionCompleted(event);
                break;
            case "payment_intent.succeeded":
                stripeService.handlePaymentIntentSucceeded(event);
                break;
            default:
                System.out.println("Unhandled event type: " + event.getType());
        }

        return "";
    }
}

