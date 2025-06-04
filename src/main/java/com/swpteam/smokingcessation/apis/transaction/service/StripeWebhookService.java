package com.swpteam.smokingcessation.apis.transaction.service;

import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.Subscription;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StripeWebhookService {
    public void handleSubscriptionCreated(EventDataObjectDeserializer deserializer) {
        Subscription subscription = (Subscription) deserializer.getObject().get();
        System.out.println("Subscription created: " + subscription.getId());
        // Add your business logic here (e.g., update database, send notifications)
    }

    public void handleSubscriptionUpdated(EventDataObjectDeserializer deserializer) {
        Subscription subscription = (Subscription) deserializer.getObject().get();
        System.out.println("Subscription updated: " + subscription.getId());
        // Add your business logic here
    }

    public void handleSubscriptionDeleted(EventDataObjectDeserializer deserializer) {
        Subscription subscription = (Subscription) deserializer.getObject().get();
        System.out.println("Subscription deleted: " + subscription.getId());
        // Add your business logic here
    }
}
