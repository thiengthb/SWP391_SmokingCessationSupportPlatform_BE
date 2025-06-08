package com.swpteam.smokingcessation.apis.transaction.controller;

import com.swpteam.smokingcessation.apis.transaction.dto.request.StripeSubscriptionRequest;
import com.swpteam.smokingcessation.apis.transaction.dto.response.StripeResponse;
import com.swpteam.smokingcessation.apis.transaction.service.StripeService;
import com.swpteam.smokingcessation.common.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stripe-payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StripePaymentController {
    StripeService stripeService;

    @PostMapping("/checkout")
    public ApiResponse<StripeResponse> checkoutSubscription(@RequestBody StripeSubscriptionRequest request) {
        return ApiResponse.<StripeResponse>builder()
                .message("Checkout Success")
                .result(stripeService.checkoutSubscription(request))
                .build();
    }

}
