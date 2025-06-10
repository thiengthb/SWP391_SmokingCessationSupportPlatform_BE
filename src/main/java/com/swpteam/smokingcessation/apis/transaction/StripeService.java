package com.swpteam.smokingcessation.apis.transaction;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.swpteam.smokingcessation.apis.membership.Membership;
import com.swpteam.smokingcessation.apis.membership.MembershipRepository;
import com.swpteam.smokingcessation.apis.transaction.dto.request.StripeSubscriptionRequest;
import com.swpteam.smokingcessation.apis.transaction.dto.response.StripeResponse;
import com.swpteam.smokingcessation.constants.ErrorCode;
import com.swpteam.smokingcessation.exception.AppException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StripeService {
    MembershipRepository membershipRepository;

    public StripeResponse checkoutSubscription(StripeSubscriptionRequest request) {
        Membership membership = membershipRepository.findByNameAndIsDeletedFalse(request.getName())
                .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND));

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(request.getName())
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD")
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
                        .setSuccessUrl("http://localhost:8080/success")
                        .setCancelUrl("http://localhost:8080/cancel")
                        .addLineItem(lineItem)
                        .build();

        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            e.printStackTrace();
        }

        return StripeResponse.builder()
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }
}
