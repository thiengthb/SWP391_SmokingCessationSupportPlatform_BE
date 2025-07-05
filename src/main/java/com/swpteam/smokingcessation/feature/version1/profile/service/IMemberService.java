package com.swpteam.smokingcessation.feature.version1.profile.service;

import com.swpteam.smokingcessation.domain.dto.member.MemberCreateRequest;
import com.swpteam.smokingcessation.domain.dto.member.MemberProfileResponse;
import com.swpteam.smokingcessation.domain.dto.member.MemberUpdateRequest;
import com.swpteam.smokingcessation.domain.dto.member.ProgressResponse;
import com.swpteam.smokingcessation.domain.entity.Member;

import java.util.List;

public interface IMemberService {

    MemberProfileResponse getMyMemberProfile();

    MemberProfileResponse getMemberById(String accountId);

    MemberProfileResponse createMember(MemberCreateRequest request);

    MemberProfileResponse updateMemberById(String accountId, MemberUpdateRequest request);

    MemberProfileResponse updateMyMemberProfile(MemberUpdateRequest request);

    Member findMemberByIdOrThrowError(String id);

    List<Member> findAllMember();

    ProgressResponse getProgress();
}
