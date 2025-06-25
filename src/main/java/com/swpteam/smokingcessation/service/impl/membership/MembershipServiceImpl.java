package com.swpteam.smokingcessation.service.impl.membership;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipCreateRequest;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipCurrencyUpdateRequest;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipResponse;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Subscription;
import com.swpteam.smokingcessation.domain.mapper.MembershipMapper;
import com.swpteam.smokingcessation.integration.currency.ICurrencyRateService;
import com.swpteam.smokingcessation.repository.MembershipRepository;
import com.swpteam.smokingcessation.repository.SubscriptionRepository;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Membership;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.service.interfaces.membership.IMembershipService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Cacheable(value = "MEMBERSHIP_LIST_CACHE")
    public List<MembershipResponse> getCategoryList() {
        return membershipRepository.findAll(Sort.by("name")).stream().map(membershipMapper::toResponse).toList();
    }

    @Override
    @Cacheable(value = "MEMBERSHIP_PAGE_CACHE",
            key = "#request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction")
    public PageResponse<MembershipResponse> getMembershipPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Membership.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Membership> memberships = membershipRepository.findAllByIsDeletedFalse(pageable);

        return new PageResponse<>(memberships.map(membershipMapper::toResponse));
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
    @CacheEvict(value = "MEMBERSHIP_PAGE_CACHE", allEntries = true)
    public MembershipResponse createMembership(MembershipCreateRequest request) {
        if (membershipRepository.existsByNameAndIsDeletedFalse(request.name()))
            throw new AppException(ErrorCode.MEMBERSHIP_NAME_UNIQUE);

        Membership membership = membershipMapper.toEntity(request);

        return membershipMapper.toResponse(membershipRepository.save(membership));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CachePut(value = "MEMBERSHIP_CACHE", key = "#result.getId()")
    @CacheEvict(value = "MEMBERSHIP_PAGE_CACHE", allEntries = true)
    public MembershipResponse updateMembership(String id, MembershipUpdateRequest request) {
        Membership membership = findMembershipByIdOrThrowError(id);

        membershipMapper.update(membership, request);

        return membershipMapper.toResponse(membershipRepository.save(membership));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = {"MEMBERSHIP_CACHE", "MEMBERSHIP_PAGE_CACHE"}, key = "#id", allEntries = true)
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
    @CacheEvict(value = "MEMBERSHIP_PAGE_CACHE", allEntries = true)
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
