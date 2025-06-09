package com.swpteam.smokingcessation.apis.membership.service;

import com.swpteam.smokingcessation.apis.membership.dto.request.MembershipCreationRequest;
import com.swpteam.smokingcessation.apis.membership.dto.request.MembershipUpdateRequest;
import com.swpteam.smokingcessation.apis.membership.dto.response.MembershipResponse;
import com.swpteam.smokingcessation.apis.membership.entity.Membership;
import com.swpteam.smokingcessation.apis.membership.mapper.MembershipMapper;
import com.swpteam.smokingcessation.apis.membership.repository.MembershipRepository;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MembershipService {
    MembershipRepository membershipRepository;
    MembershipMapper membershipMapper;

    public MembershipResponse createMembership(MembershipCreationRequest request) {
        Membership membership = membershipMapper.toEntity(request);
        membership.setCreatedAt(LocalDateTime.now());

        return membershipMapper.toResponse(membershipRepository.save(membership));
    }

    public MembershipResponse updateMembership(String name, MembershipUpdateRequest request) {
        Membership membership = membershipRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_EXISTED));

        membershipMapper.updateMembership(membership, request);
        membership.setUpdatedAt(LocalDateTime.now());

        return membershipMapper.toResponse(membershipRepository.save(membership));
    }

    public void deleteMembership(String name) {
        membershipRepository.deleteById(name);
    }

    public List<MembershipResponse> getMembershipList() {
        return membershipRepository.findAll().stream().map(membershipMapper::toResponse).toList();
    }

    public MembershipResponse getMembership(String name) {
        return membershipMapper.toResponse(
                membershipRepository.findByName(name)
                        .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_EXISTED)));
    }
}
