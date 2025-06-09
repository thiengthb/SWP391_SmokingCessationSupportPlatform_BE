package com.swpteam.smokingcessation.apis.subscription;

import com.swpteam.smokingcessation.apis.subscription.dto.SubscriptionRequest;
import com.swpteam.smokingcessation.apis.subscription.dto.SubscriptionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    Subscription toEntity(SubscriptionRequest request);

    SubscriptionResponse toResponse(Subscription Subscription);
}
