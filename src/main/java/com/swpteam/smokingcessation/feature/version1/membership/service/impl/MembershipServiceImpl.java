package com.swpteam.smokingcessation.feature.version1.membership.service.impl;

import com.swpteam.smokingcessation.domain.dto.membership.MembershipCreateRequest;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipCurrencyUpdateRequest;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipResponse;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Subscription;
import com.swpteam.smokingcessation.domain.mapper.MembershipMapper;
import com.swpteam.smokingcessation.feature.integration.currency.ICurrencyRateService;
import com.swpteam.smokingcessation.repository.jpa.MembershipRepository;
import com.swpteam.smokingcessation.repository.jpa.SubscriptionRepository;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Membership;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.feature.version1.membership.service.IMembershipService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MembershipServiceImpl implements IMembershipService {

    MembershipRepository membershipRepository;
    MembershipMapper membershipMapper;
    SubscriptionRepository subscriptionRepository;
    ICurrencyRateService currencyRateService;

    @Override
    public List<MembershipResponse> getMembershipList() {
        return membershipRepository.findAll(Sort.by("price")).stream().map(membershipMapper::toResponse).toList();
    }

    @Override
    public MembershipResponse getMembershipById(String id) {
        return membershipMapper.toResponse(findMembershipByIdOrThrowError(id));
    }

    @Override
    public MembershipResponse getMembershipByName(String name) {
        return membershipMapper.toResponse(findMembershipByNameOrThrowError(name));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CachePut(value = "MEMBERSHIP_CACHE", key = "#result.getId()")
    public MembershipResponse createMembership(MembershipCreateRequest request) {
        if (membershipRepository.existsByNameAndIsDeletedFalse(request.name()))
            throw new AppException(ErrorCode.MEMBERSHIP_NAME_DUPLICATE);

        Membership membership = membershipMapper.toEntity(request);

        return membershipMapper.toResponse(membershipRepository.save(membership));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CachePut(value = "MEMBERSHIP_CACHE", key = "#result.getId()")
    public MembershipResponse updateMembership(String id, MembershipUpdateRequest request) {
        Membership membership = findMembershipByIdOrThrowError(id);

        membershipMapper.update(membership, request);

        return membershipMapper.toResponse(membershipRepository.save(membership));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "MEMBERSHIP_CACHE", key = "#id")
    public void softDeleteMembershipById(String id) {
        Membership membership = findMembershipByIdOrThrowError(id);
        membership.setDeleted(true);

        List<Subscription> subscriptions = membership.getSubscriptions();
        subscriptions.forEach(subscription -> subscription.setDeleted(true));
        subscriptionRepository.saveAll(subscriptions);

        membershipRepository.save(membership);
    }

    @Override
    @Transactional
    @CachePut(value = "MEMBERSHIP_CACHE", key = "#result.getId()")
    public MembershipResponse updateMembershipCurrency(String id, MembershipCurrencyUpdateRequest request) {
        Double rate = currencyRateService.getRate(request.currency().name().toUpperCase());
        if (rate == null)
            throw new AppException(ErrorCode.INVALID_CURRENCY);

        Membership membership = findMembershipByIdOrThrowError(id);

        double newPrice = currencyRateService.getNewPrice(
                membership.getPrice(),
                membership.getCurrency().name().toUpperCase(),
                request.currency().name().toUpperCase()
        );

        membership.setCurrency(request.currency());
        membership.setPrice(newPrice);

        return membershipMapper.toResponse(membershipRepository.save(membership));
    }

    @Override
    public Membership findMembershipByIdOrThrowError(String id) {
        return membershipRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND));
    }

    @Override
    public Membership findMembershipByNameOrThrowError(String name) {
        return membershipRepository.findByNameAndIsDeletedFalse(name)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND));
    }

}
