package com.swpteam.smokingcessation.apis.subscription;

import com.swpteam.smokingcessation.apis.subscription.dto.SubscriptionRequest;
import com.swpteam.smokingcessation.apis.subscription.dto.SubscriptionResponse;
import com.swpteam.smokingcessation.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SubscriptionController {
    SubscriptionService subscriptionService;

    @PostMapping
    ApiResponse<SubscriptionResponse> createSubscription(@RequestBody @Valid SubscriptionRequest request) {
        return ApiResponse.<SubscriptionResponse>builder()
                .result(subscriptionService.createSubscription(request))
                .build();
    }

    @PutMapping("/{subscriptionId}")
    ApiResponse<SubscriptionResponse> updateSubscription(@PathVariable String subscriptionId, @RequestBody @Valid SubscriptionRequest request) {
        return ApiResponse.<SubscriptionResponse>builder()
                .result(subscriptionService.updateSubscription(subscriptionId, request))
                .build();
    }

    @DeleteMapping("/{subscriptionId}")
    ApiResponse<String> createSubscription(@PathVariable String membershipName) {
        subscriptionService.deleteSubscription(membershipName);
        return ApiResponse.<String>builder()
                .result("Subscription has been deleted")
                .build();
    }

    @GetMapping
    ApiResponse<List<SubscriptionResponse>> getSubscriptionList() {
        return ApiResponse.<List<SubscriptionResponse>>builder()
                .result(subscriptionService.getSubscriptionList())
                .build();
    }

    @GetMapping("/{subscriptionId}")
    ApiResponse<SubscriptionResponse> getSubscriptionList(@PathVariable String subscriptionId) {
        return ApiResponse.<SubscriptionResponse>builder()
                .result(subscriptionService.getSubscription(subscriptionId))
                .build();
    }
}