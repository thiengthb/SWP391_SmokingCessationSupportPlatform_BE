package com.swpteam.smokingcessation.apis.subscription;

import com.swpteam.smokingcessation.apis.subscription.dto.SubscriptionRequest;
import com.swpteam.smokingcessation.apis.subscription.dto.SubscriptionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    Subscription toEntity(SubscriptionRequest request);

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "membership.name", target = "membershipName")
    SubscriptionResponse toResponse(Subscription Subscription);

    void update(@MappingTarget Subscription Subscription, SubscriptionRequest request);
}
