package com.swpteam.smokingcessation.service.interfaces.profile;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.member.MemberRequest;
import com.swpteam.smokingcessation.domain.dto.member.MemberResponse;
import org.springframework.data.domain.Page;

public interface IMemberService {

    MemberResponse createMember(MemberRequest request, String id);

    Page<MemberResponse> getMembers(PageableRequest request);

    MemberResponse getMemberById(String id);

    MemberResponse updateMember(MemberRequest request, String id);
}
