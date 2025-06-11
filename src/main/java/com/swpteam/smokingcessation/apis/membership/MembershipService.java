package com.swpteam.smokingcessation.apis.membership;

import com.swpteam.smokingcessation.apis.membership.dto.MembershipRequest;
import com.swpteam.smokingcessation.apis.membership.dto.MembershipResponse;
import com.swpteam.smokingcessation.apis.subscription.Subscription;
import com.swpteam.smokingcessation.apis.subscription.SubscriptionRepository;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.constants.ErrorCode;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MembershipService {

    MembershipRepository membershipRepository;
    MembershipMapper membershipMapper;

    SubscriptionRepository subscriptionRepository;

    public Page<MembershipResponse> getMembershipPage(PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Membership.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Membership> memberships = membershipRepository.findAllByIsDeletedFalse(pageable);

        return memberships.map(membershipMapper::toResponse);
    }

    public MembershipResponse getMembershipById(String id) {
        return membershipMapper.toResponse(
                membershipRepository.findByIdAndIsDeletedFalse(id)
                        .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND)));
    }

    public MembershipResponse getMembershipByName(String name) {
        return membershipMapper.toResponse(
                membershipRepository.findByNameAndIsDeletedFalse(name)
                        .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND)));
    }

    @Transactional
    public MembershipResponse createMembership(MembershipRequest request) {
        if (membershipRepository.existsByNameAndIsDeletedFalse(request.getName()))
            throw new AppException(ErrorCode.MEMBERSHIP_NAME_UNIQUE);

        Membership membership = membershipMapper.toEntity(request);

        return membershipMapper.toResponse(membershipRepository.save(membership));
    }

    @Transactional
    public MembershipResponse updateMembership(String id, MembershipRequest request) {
        Membership membership = membershipRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND));

        membershipMapper.update(membership, request);

        return membershipMapper.toResponse(membershipRepository.save(membership));
    }

    @Transactional
    public void softDeleteMembershipById(String id) {
        Membership membership = membershipRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND));

        membership.setDeleted(true);

        List<Subscription> subscriptions = membership.getSubscriptions();
        subscriptions.forEach(subscription -> subscription.setDeleted(true));

        subscriptionRepository.saveAll(subscriptions);
        membershipRepository.save(membership);
    }

}
