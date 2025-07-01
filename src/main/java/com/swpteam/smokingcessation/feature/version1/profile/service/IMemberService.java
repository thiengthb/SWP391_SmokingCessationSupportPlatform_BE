package com.swpteam.smokingcessation.feature.version1.profile.service;

import com.swpteam.smokingcessation.domain.dto.member.MemberRequest;
import com.swpteam.smokingcessation.domain.dto.member.MemberResponse;
import com.swpteam.smokingcessation.domain.dto.member.ProgressResponse;
import com.swpteam.smokingcessation.domain.entity.Member;

import java.util.List;

public interface IMemberService {

    MemberResponse createMember(MemberRequest request);

    MemberResponse getMemberById(String accountId);

    MemberResponse updateMemberById(String accountId, MemberRequest request);

    MemberResponse updateMyMemberProfile(MemberRequest request);

    Member findMemberByIdOrThrowError(String id);

    List<Member> findAllMember();

    ProgressResponse getProgress();
}
