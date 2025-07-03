package com.swpteam.smokingcessation.feature.version1.membership.service;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.subscription.SubscriptionRequest;
import com.swpteam.smokingcessation.domain.dto.subscription.SubscriptionResponse;
import com.swpteam.smokingcessation.domain.entity.Subscription;

public interface ISubscriptionService {

    PageResponse<SubscriptionResponse> getSubscriptionPage(PageableRequest request);

    PageResponse<SubscriptionResponse> getMySubscriptionPage(PageableRequest request);

    PageResponse<SubscriptionResponse> getSubscriptionPageByAccountId(String accountId, PageableRequest request);

    SubscriptionResponse getSubscriptionById(String id);

    Subscription createSubscription(String accountId, String membershipName);

    SubscriptionResponse createSubscription(SubscriptionRequest request);

    SubscriptionResponse updateSubscription(String id, SubscriptionRequest request);

    void softDeleteSubscription(String id);

    Subscription findSubscriptionByIdOrThrowError(String id);
}
