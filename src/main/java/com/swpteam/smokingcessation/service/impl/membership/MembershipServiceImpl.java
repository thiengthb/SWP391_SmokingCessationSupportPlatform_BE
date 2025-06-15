package com.swpteam.smokingcessation.service.impl.membership;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public Page<MembershipResponse> getMembershipPage(PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Membership.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Membership> memberships = membershipRepository.findAllByIsDeletedFalse(pageable);

        return memberships.map(membershipMapper::toResponse);
    }

    @Override
    public MembershipResponse getMembershipById(String id) {
        return membershipMapper.toResponse(
                membershipRepository.findByIdAndIsDeletedFalse(id)
                        .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND)));
    }

    @Override
    public MembershipResponse getMembershipByName(String name) {
        return membershipMapper.toResponse(
                membershipRepository.findByNameAndIsDeletedFalse(name)
                        .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND)));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public MembershipResponse createMembership(MembershipCreateRequest request) {
        if (membershipRepository.existsByNameAndIsDeletedFalse(request.getName()))
            throw new AppException(ErrorCode.MEMBERSHIP_NAME_UNIQUE);

        Membership membership = membershipMapper.toEntity(request);
        membership.setCreatedAt(LocalDateTime.now());

        return membershipMapper.toResponse(membershipRepository.save(membership));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public MembershipResponse updateMembership(String id, MembershipUpdateRequest request) {
        Membership membership = membershipRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND));

        membershipMapper.update(membership, request);

        return membershipMapper.toResponse(membershipRepository.save(membership));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void softDeleteMembershipById(String id) {
        Membership membership = membershipRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND));

        membership.setDeleted(true);

        List<Subscription> subscriptions = membership.getSubscriptions();
        subscriptions.forEach(subscription -> subscription.setDeleted(true));

        subscriptionRepository.saveAll(subscriptions);
        membershipRepository.save(membership);
    }

    @Override
    @Transactional
    public MembershipResponse updateMembershipCurrency(String id, MembershipCurrencyUpdateRequest request) {
        Double rate = currencyRateService.getRate(request.getCurrency().name().toUpperCase());
        if (rate == null)
            throw new AppException(ErrorCode.INVALID_CURRENCY);

        Membership membership = membershipRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND));

        double newPrice = currencyRateService.getNewPrice(
                membership.getPrice(),
                membership.getCurrency().name().toUpperCase(),
                request.getCurrency().name().toUpperCase()
        );

        membership.setCurrency(request.getCurrency());
        membership.setPrice(newPrice);

        return membershipMapper.toResponse(membershipRepository.save(membership));
    }

}
