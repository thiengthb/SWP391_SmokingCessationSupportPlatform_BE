package com.swpteam.smokingcessation.integration.payment.services;

import com.stripe.model.Event;
import com.swpteam.smokingcessation.integration.payment.dto.StripeResponse;
import com.swpteam.smokingcessation.integration.payment.dto.StripeSubscriptionRequest;

public interface IStripeService {

    StripeResponse checkoutSubscription(StripeSubscriptionRequest request);

    void handleCheckoutSessionCompleted(Event event);

    void handlePaymentIntentSucceeded(Event event);
}
