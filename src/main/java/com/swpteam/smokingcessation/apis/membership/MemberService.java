package com.swpteam.smokingcessation.apis.membership;

import com.swpteam.smokingcessation.apis.membership.dto.MembershipCreationRequest;
import com.swpteam.smokingcessation.apis.membership.dto.MembershipUpdateRequest;
import com.swpteam.smokingcessation.apis.membership.dto.MembershipResponse;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.constants.ErrorCode;
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
public class MemberService {
    MembershipRepository membershipRepository;
    MembershipMapper membershipMapper;

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

    public List<MembershipResponse> getMembershipList() {
        return membershipRepository.findAll().stream().map(membershipMapper::toResponse).toList();
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
}
