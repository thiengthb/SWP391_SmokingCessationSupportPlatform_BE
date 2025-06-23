package com.swpteam.smokingcessation.service.interfaces.profile;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.member.MemberRequest;
import com.swpteam.smokingcessation.domain.dto.member.MemberResponse;
import com.swpteam.smokingcessation.domain.entity.Member;
import org.springframework.data.domain.Page;

public interface IMemberService {

    MemberResponse createMember(MemberRequest request);

    PageResponse<MemberResponse> getMembersPage(PageableRequest request);

    MemberResponse getMemberById(String accountId);

    MemberResponse updateMemberById(String accountId, MemberRequest request);

    MemberResponse updateMyMemberProfile(MemberRequest request);

    Member findMemberByIdOrThrowError(String id);
}
