package com.swpteam.smokingcessation.controller.v1.payment;

import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.payment.StripeResponse;
import com.swpteam.smokingcessation.domain.dto.payment.StripeSubscriptionRequest;
import com.swpteam.smokingcessation.integration.payment.StripeService;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/stripe-payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Stripe Payment", description = "Manage payment-related operations")
public class StripePaymentController {

    StripeService stripeService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<StripeResponse>> checkoutSubscription(
            @RequestBody StripeSubscriptionRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.CHECKOUT_SUCCESS,
                stripeService.checkoutSubscription(request)
        );
    }

}
