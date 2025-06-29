package com.swpteam.smokingcessation.service.interfaces.profile;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.member.MemberRequest;
import com.swpteam.smokingcessation.domain.dto.member.MemberResponse;
import com.swpteam.smokingcessation.domain.entity.Member;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IMemberService {

    MemberResponse createMember(MemberRequest request);

    MemberResponse getMemberById(String accountId);

    MemberResponse updateMemberById(String accountId, MemberRequest request);

    MemberResponse updateMyMemberProfile(MemberRequest request);

    Member findMemberByIdOrThrowError(String id);

    List<Member> findAllMember();
}
