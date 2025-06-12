package com.swpteam.smokingcessation.apis.transaction;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.swpteam.smokingcessation.apis.membership.Membership;
import com.swpteam.smokingcessation.apis.membership.MembershipRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StripeService {

    @NonFinal
    @Value("${stripe.success-url}")
    protected String successUrl;

    @NonFinal
    @Value("${stripe.cancel-url}")
    protected String cancelUrl;

    MembershipRepository membershipRepository;

    public StripeResponse checkoutSubscription(StripeSubscriptionRequest request) {
        Membership membership = membershipRepository.findByNameAndIsDeletedFalse(request.getMembershipName())
                .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND));

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
}
