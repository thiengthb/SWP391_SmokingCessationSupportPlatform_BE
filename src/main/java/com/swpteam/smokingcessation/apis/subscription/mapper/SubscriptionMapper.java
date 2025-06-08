package com.swpteam.smokingcessation.apis.subscription.mapper;

import com.swpteam.smokingcessation.apis.subscription.dto.request.SubscriptionRequest;
import com.swpteam.smokingcessation.apis.subscription.dto.response.SubscriptionResponse;
import com.swpteam.smokingcessation.apis.subscription.entity.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    Subscription toEntity(SubscriptionRequest request);

    SubscriptionResponse toResponse(Subscription Subscription);
}
