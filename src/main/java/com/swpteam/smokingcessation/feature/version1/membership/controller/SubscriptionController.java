package com.swpteam.smokingcessation.feature.version1.membership.controller;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.domain.dto.subscription.SubscriptionRequest;
import com.swpteam.smokingcessation.domain.dto.subscription.SubscriptionResponse;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.feature.version1.membership.service.ISubscriptionService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Subscription", description = "Manage subscription-related operations")
public class SubscriptionController {

    ISubscriptionService subscriptionService;
    ResponseUtilService responseUtilService;

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<SubscriptionResponse>>> getSubscriptionPage(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.SUBSCRIPTION_PAGE_FETCHED,
                subscriptionService.getSubscriptionPage(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<SubscriptionResponse>> getSubscriptionById(
            @PathVariable String id
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.SUBSCRIPTION_FETCHED_BY_ID,
                subscriptionService.getSubscriptionById(id)
        );
    }

    @GetMapping("/account/{id}")
    ResponseEntity<ApiResponse<PageResponse<SubscriptionResponse>>> getSubscriptionPageByAccountId(
            @PathVariable String id,
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.SUBSCRIPTION_FETCHED_BY_ACCOUNT,
                subscriptionService.getSubscriptionPageByAccountId(id, request)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<SubscriptionResponse>> createSubscription(
            @RequestBody @Valid SubscriptionRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.SUBSCRIPTION_CREATED,
                subscriptionService.createSubscription(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<SubscriptionResponse>> updateSubscription(
            @PathVariable String id,
            @RequestBody @Valid SubscriptionRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.SUBSCRIPTION_UPDATED,
                subscriptionService.updateSubscription(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteSubscription(
            @PathVariable String id
    ) {
        subscriptionService.softDeleteSubscription(id);
        return responseUtilService.buildSuccessResponse(
                SuccessCode.SUBSCRIPTION_DELETED
        );
    }
}
