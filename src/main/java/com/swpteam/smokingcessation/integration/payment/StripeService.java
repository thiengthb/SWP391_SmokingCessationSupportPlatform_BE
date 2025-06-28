package com.swpteam.smokingcessation.integration.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.integration.mail.MailServiceImpl;
import com.swpteam.smokingcessation.domain.entity.Membership;
import com.swpteam.smokingcessation.domain.entity.Subscription;
import com.swpteam.smokingcessation.service.impl.membership.SubscriptionServiceImpl;
import com.swpteam.smokingcessation.domain.entity.Transaction;
import com.swpteam.smokingcessation.domain.dto.payment.StripeResponse;
import com.swpteam.smokingcessation.domain.dto.payment.StripeSubscriptionRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.service.impl.membership.TransactionServiceImpl;
import com.swpteam.smokingcessation.service.interfaces.identity.IAccountService;
import com.swpteam.smokingcessation.service.interfaces.membership.IMembershipService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StripeService implements IStripeService {

    TransactionServiceImpl transactionService;
    SubscriptionServiceImpl subscriptionService;
    MailServiceImpl mailServiceImpl;

    IAccountService accountService;
    IMembershipService membershipService;

    @NonFinal
    @Value("${stripe.success-url}")
    protected String successUrl;

    @NonFinal
    @Value("${stripe.cancel-url}")
    protected String cancelUrl;

    @Override
    public StripeResponse checkoutSubscription(StripeSubscriptionRequest request) {
        Membership membership = membershipService.findMembershipByNameOrThrowError(request.membershipName());

        Account account = accountService.findAccountByIdOrThrowError(request.accountId());

        Transaction transaction = transactionService.createTransaction(account, membership.getPrice());

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(membership.getName())
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(membership.getCurrency() != null ? membership.getCurrency().name().toUpperCase() : "USD")
                        .setUnitAmount((long) membership.getPrice())
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams
                        .LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(successUrl)
                        .setCancelUrl(cancelUrl)
                        .putMetadata("accountId", account.getId())
                        .putMetadata("membershipName", membership.getName())
                        .putMetadata("transactionId", transaction.getId())
                        .addLineItem(lineItem)
                        .build();

        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new AppException(ErrorCode.UNCATEGORIZED_ERROR);
        }

        assert session != null;
        return StripeResponse.builder()
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }

    @Override
    public void handleCheckoutSessionCompleted(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        if (deserializer.getObject().isPresent()) {
            Object raw = deserializer.getObject().get();

            if (raw instanceof Session session) {
                log.info("Deserialized object type: {}", session.getClass());

                if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
                    log.warn("Session payment_status is not 'paid': {}", session.getPaymentStatus());
                    return;
                }

                Map<String, String> metadata = session.getMetadata();
                if (metadata == null || metadata.isEmpty()) {
                    log.warn("Session metadata is empty");
                    return;
                }

                String accountId = metadata.get("accountId");
                String membershipName = metadata.get("membershipName");
                String transactionId = metadata.get("transactionId");

                if (accountId == null || membershipName == null || transactionId == null) {
                    log.error("Missing required metadata: {}", metadata);
                    return;
                }

                Membership membership = membershipService.findMembershipByNameOrThrowError(membershipName);

                transactionService.makeAsPaid(transactionId);

                Subscription subscription = subscriptionService.createSubscription(accountId, membershipName);

                mailServiceImpl.sendPaymentSuccessEmail(accountId, subscription.getId(), membership.getPrice());
            } else {
                log.error("Unexpected object type in checkout.session.completed: {}", raw.getClass());
            }
        } else {
            log.warn("Unable to deserialize checkout.session.completed event.");
        }
    }

    @Override
    public void handlePaymentIntentSucceeded(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        if (deserializer.getObject().isPresent()) {
            Object raw = deserializer.getObject().get();

            if (raw instanceof PaymentIntent paymentIntent) {
                log.info("Deserialized object type: {}", paymentIntent.getClass());

                if (!"succeeded".equalsIgnoreCase(paymentIntent.getStatus())) {
                    log.warn("PaymentIntent status is not 'succeeded': {}", paymentIntent.getStatus());
                    return;
                }

                Map<String, String> metadata = paymentIntent.getMetadata();
                if (metadata == null || metadata.isEmpty()) {
                    log.warn("PaymentIntent metadata is missing");
                    return;
                }

                String accountId = metadata.get("accountId");
                String membershipName = metadata.get("membershipName");
                String transactionId = metadata.get("transactionId");

                if (accountId == null || membershipName == null || transactionId == null) {
                    log.error("Missing required metadata: {}", metadata);
                    return;
                }

                accountService.findAccountByIdOrThrowError(accountId);

                Membership membership = membershipService.findMembershipByNameOrThrowError(membershipName);

                transactionService.makeAsPaid(transactionId);
                Subscription subscription = subscriptionService.createSubscription(accountId, membershipName);

                mailServiceImpl.sendPaymentSuccessEmail(accountId, subscription.getId(), membership.getPrice());
            } else {
                log.error("Unexpected object type in payment_intent.succeeded: {}", raw.getClass());
            }
        } else {
            log.warn("Unable to deserialize payment_intent.succeeded event.");
        }
    }

}
