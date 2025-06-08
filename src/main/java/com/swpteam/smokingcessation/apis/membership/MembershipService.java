package com.swpteam.smokingcessation.apis.membership;

import com.swpteam.smokingcessation.apis.membership.dto.MembershipCreationRequest;
import com.swpteam.smokingcessation.apis.membership.dto.MembershipUpdateRequest;
import com.swpteam.smokingcessation.apis.membership.dto.MembershipResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.constants.ErrorCode;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MembershipService {
    MembershipRepository membershipRepository;
    MembershipMapper membershipMapper;

    public Page<MembershipResponse> getMembershipPage(PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Membership.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Membership> memberships = membershipRepository.findAll(pageable);

        return memberships.map(membershipMapper::toResponse);
    }

    public MembershipResponse getMembershipByName(String name) {
        return membershipMapper.toResponse(
                membershipRepository.findByName(name)
                        .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_EXISTED)));
    }

    public MembershipResponse getMembershipById(String id) {
        return membershipMapper.toResponse(
                membershipRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_EXISTED)));
    }

    public MembershipResponse createMembership(MembershipCreationRequest request) {
        if (membershipRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.MEMBERSHIP_NAME_UNIQUE);

        Membership membership = membershipMapper.toEntity(request);

        return membershipMapper.toResponse(membershipRepository.save(membership));
    }

    public MembershipResponse updateMembership(String id, MembershipUpdateRequest request) {
        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_EXISTED));

        membershipMapper.updateMembership(membership, request);

        return membershipMapper.toResponse(membershipRepository.save(membership));
    }

    public void deleteMembership(String id) {
        membershipRepository.deleteById(id);
    }
}
