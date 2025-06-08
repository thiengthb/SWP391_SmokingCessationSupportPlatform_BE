package com.swpteam.smokingcessation.apis.subscription.service;

import com.swpteam.smokingcessation.apis.account.AccountRepository;
import com.swpteam.smokingcessation.apis.membership.repository.MembershipRepository;
import com.swpteam.smokingcessation.apis.subscription.dto.request.SubscriptionRequest;
import com.swpteam.smokingcessation.apis.subscription.dto.response.SubscriptionResponse;
import com.swpteam.smokingcessation.apis.subscription.entity.Subscription;
import com.swpteam.smokingcessation.apis.subscription.mapper.SubscriptionMapper;
import com.swpteam.smokingcessation.apis.subscription.repository.SubscriptionRepository;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SubscriptionService {
    SubscriptionRepository subscriptionRepository;
    SubscriptionMapper subscriptionMapper;

    AccountRepository accountRepository;
    MembershipRepository membershipRepository;

    public SubscriptionResponse createSubscription(SubscriptionRequest request) {
        Subscription subscription = subscriptionMapper.toEntity(request);

        accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));

        membershipRepository.findByName(request.getMembershipName())
                .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_EXISTED));

        return subscriptionMapper.toResponse(subscriptionRepository.save(subscription));
    }

    public SubscriptionResponse updateSubscription(String id, SubscriptionRequest request) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_NOT_EXISTED));

        return subscriptionMapper.toResponse(subscriptionRepository.save(subscription));
    }

    public void deleteSubscription(String id) {
        subscriptionRepository.deleteById(id);
    }

    public List<SubscriptionResponse> getSubscriptionList() {
        return subscriptionRepository.findAll().stream().map(subscriptionMapper::toResponse).toList();
    }

    public SubscriptionResponse getSubscription(String id) {
        return subscriptionMapper.toResponse(
                subscriptionRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_NOT_EXISTED)));
    }
}
