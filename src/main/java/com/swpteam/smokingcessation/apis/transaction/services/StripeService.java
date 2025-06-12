package com.swpteam.smokingcessation.apis.transaction.services;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.account.AccountRepository;
import com.swpteam.smokingcessation.apis.mail.MailService;
import com.swpteam.smokingcessation.apis.membership.Membership;
import com.swpteam.smokingcessation.apis.membership.MembershipRepository;
import com.swpteam.smokingcessation.apis.subscription.Subscription;
import com.swpteam.smokingcessation.apis.subscription.SubscriptionRepository;
import com.swpteam.smokingcessation.apis.subscription.SubscriptionService;
import com.swpteam.smokingcessation.apis.transaction.Transaction;
import com.swpteam.smokingcessation.apis.transaction.dto.StripeSubscriptionRequest;
import com.swpteam.smokingcessation.apis.transaction.dto.StripeResponse;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.constants.ErrorCode;
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
public class StripeService {
    private final SubscriptionRepository subscriptionRepository;

    @NonFinal
    @Value("${stripe.success-url}")
    protected String successUrl;

    @NonFinal
    @Value("${stripe.cancel-url}")
    protected String cancelUrl;

    AccountRepository accountRepository;
    MembershipRepository membershipRepository;

    TransactionService transactionService;
    SubscriptionService subscriptionService;
    MailService mailService;

    public StripeResponse checkoutSubscription(StripeSubscriptionRequest request) {
        Membership membership = membershipRepository.findByNameAndIsDeletedFalse(request.getMembershipName())
                .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND));

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        Transaction transaction = transactionService.createTransaction(account, membership.getPrice());

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(membership.getName())
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(membership.getCurrency() != null ? membership.getCurrency().name().toUpperCase()  : "USD")
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
            e.printStackTrace();
        }

        assert session != null;
        return StripeResponse.builder()
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }

    public void handleCheckoutSessionCompleted(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

        if (deserializer.getObject().isPresent()) {
            Object raw = deserializer.getObject().get();

            if (raw instanceof Session session) {
                if (!"paid".equals(session.getPaymentStatus())) return;

                String accountId = session.getMetadata().get("accountId");
                String membershipName = session.getMetadata().get("membershipName");
                String transactionId = session.getMetadata().get("transactionId");

                Account account = accountRepository.findById(accountId)
                        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

                Membership membership = membershipRepository.findByNameAndIsDeletedFalse(membershipName)
                        .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND));

                transactionService.makeAsPaid(transactionId);
                Subscription subscription = subscriptionService.createSubscription(accountId, membershipName);

                mailService.sendPaymentSuccessEmail(account, subscription, membership.getPrice());
            } else {
                log.error("Unexpected object type in checkout.session.completed: {}", raw.getClass());
            }
        } else {
            log.warn("Unable to deserialize checkout.session.completed event.");
        }
    }

    public void handlePaymentIntentSucceeded(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

        if (deserializer.getObject().isPresent()) {
            Object raw = deserializer.getObject().get();

            if (raw instanceof PaymentIntent paymentIntent) {
                if (!"succeeded".equals(paymentIntent.getStatus())) return;

                Map<String, String> metadata = paymentIntent.getMetadata();
                if (metadata == null) {
                    log.warn("PaymentIntent has no metadata");
                    return;
                }

                String accountId = metadata.get("accountId");
                String membershipName = metadata.get("membershipName");
                String transactionId = metadata.get("transactionId");

                Account account = accountRepository.findById(accountId)
                        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

                Membership membership = membershipRepository.findByNameAndIsDeletedFalse(membershipName)
                        .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND));

                transactionService.makeAsPaid(transactionId);
                Subscription subscription = subscriptionService.createSubscription(accountId, membershipName);

                mailService.sendPaymentSuccessEmail(account, subscription, membership.getPrice());
            } else {
                log.error("Unexpected object type in payment_intent.succeeded: {}", raw.getClass());
            }
        } else {
            log.warn("Unable to deserialize payment_intent.succeeded event.");
        }
    }
}
