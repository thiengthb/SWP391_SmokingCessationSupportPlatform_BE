package com.swpteam.smokingcessation.feature.version1.membership.service;

import com.swpteam.smokingcessation.domain.dto.membership.MembershipCreateRequest;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipCurrencyUpdateRequest;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipResponse;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Membership;

import java.util.List;

public interface IMembershipService {

    List<MembershipResponse> getMembershipList();

    MembershipResponse getMembershipById(String id);

    MembershipResponse getMembershipByName(String name);

    MembershipResponse createMembership(MembershipCreateRequest request);

    MembershipResponse updateMembership(String id, MembershipUpdateRequest request);

    MembershipResponse updateMembershipCurrency(String id, MembershipCurrencyUpdateRequest request);

    Membership findMembershipByIdOrThrowError(String id);

    Membership findMembershipByNameOrThrowError(String name);

    void softDeleteMembershipById(String id);

}
