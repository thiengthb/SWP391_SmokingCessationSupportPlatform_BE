package com.swpteam.smokingcessation.integration.payment.services;

import com.stripe.model.Event;
import com.swpteam.smokingcessation.integration.payment.dto.StripeResponse;
import com.swpteam.smokingcessation.integration.payment.dto.StripeSubscriptionRequest;

public interface IStripeService {

    public StripeResponse checkoutSubscription(StripeSubscriptionRequest request);

    public void handleCheckoutSessionCompleted(Event event);

    public void handlePaymentIntentSucceeded(Event event);
}
