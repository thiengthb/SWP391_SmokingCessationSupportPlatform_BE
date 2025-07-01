package com.swpteam.smokingcessation.feature.version1.membership.service.impl;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.mapper.SubscriptionMapper;
import com.swpteam.smokingcessation.domain.entity.Membership;
import com.swpteam.smokingcessation.domain.dto.subscription.SubscriptionRequest;
import com.swpteam.smokingcessation.domain.dto.subscription.SubscriptionResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Subscription;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.jpa.SubscriptionRepository;
import com.swpteam.smokingcessation.feature.version1.identity.service.IAccountService;
import com.swpteam.smokingcessation.feature.version1.membership.service.IMembershipService;
import com.swpteam.smokingcessation.feature.version1.membership.service.ISubscriptionService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionServiceImpl implements ISubscriptionService {

    SubscriptionRepository subscriptionRepository;
    SubscriptionMapper subscriptionMapper;
    IAccountService accountService;
    IMembershipService membershipService;
    AuthUtilService authUtilService;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<SubscriptionResponse> getSubscriptionPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Subscription.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Subscription> subscriptions = subscriptionRepository.findAllByIsDeletedFalse(pageable);

        return new PageResponse<>(subscriptions.map(subscriptionMapper::toResponse));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<SubscriptionResponse> getMySubscriptionPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Subscription.class, request.sortBy());

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Subscription> subscriptions = subscriptionRepository.findByAccountIdAndIsDeletedFalse(currentAccount.getId(), pageable);

        return new PageResponse<>(subscriptions.map(subscriptionMapper::toResponse));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<SubscriptionResponse> getSubscriptionPageByAccountId(String accountId, PageableRequest request) {
        ValidationUtil.checkFieldExist(Subscription.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Subscription> subscriptions = subscriptionRepository.findByAccountIdAndIsDeletedFalse(accountId, pageable);

        return new PageResponse<>(subscriptions.map(subscriptionMapper::toResponse));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public SubscriptionResponse getSubscriptionById(String id) {
        return subscriptionMapper.toResponse(findSubscriptionByIdOrThrowError(id));
    }

    @Override
    @Transactional
    public Subscription createSubscription(String accountId, String membershipName) {
        Account account = accountService.findAccountByIdOrThrowError(accountId);

        Membership membership = membershipService.findMembershipByNameOrThrowError(membershipName);

        Subscription subscription = Subscription.builder()
                .account(account)
                .membership(membership)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(membership.getDurationDays()))
                .build();

        return subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public SubscriptionResponse createSubscription(SubscriptionRequest request) {
        Subscription subscription = subscriptionMapper.toEntity(request);

        Account account = accountService.findAccountByEmailOrThrowError(request.email());
        Membership membership = membershipService.findMembershipByNameOrThrowError(request.membershipName());

        subscription.setAccount(account);
        subscription.setMembership(membership);

        return subscriptionMapper.toResponse(subscriptionRepository.save(subscription));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public SubscriptionResponse updateSubscription(String id, SubscriptionRequest request) {
        Subscription subscription = findSubscriptionByIdOrThrowError(id);

        subscriptionMapper.update(subscription, request);

        return subscriptionMapper.toResponse(subscriptionRepository.save(subscription));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void softDeleteSubscription(String id) {
        Subscription subscription = findSubscriptionByIdOrThrowError(id);

        subscription.setDeleted(true);

        subscriptionRepository.save(subscription);
    }

    @Override
    public Subscription findSubscriptionByIdOrThrowError(String id) {
        Subscription subscription = subscriptionRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        if (subscription.getAccount().isDeleted() || subscription.getMembership().isDeleted()) {
            subscription.setDeleted(true);
            subscriptionRepository.save(subscription);
            throw new AppException(ErrorCode.SUBSCRIPTION_NOT_FOUND);
        }

        return subscription;
    }
}
