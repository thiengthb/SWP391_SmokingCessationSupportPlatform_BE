package com.swpteam.smokingcessation.apis.member.mapper;

import com.swpteam.smokingcessation.apis.member.dto.request.MemberCreateRequest;
import com.swpteam.smokingcessation.apis.member.dto.request.MemberUpdateRequest;
import com.swpteam.smokingcessation.apis.member.dto.response.MemberResponse;
import com.swpteam.smokingcessation.apis.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "score", ignore = true)
    @Mapping(target = "currentStreak", ignore = true)
    @Mapping(target = "lastCounterReset", ignore = true)
    Member toMember(MemberCreateRequest request);

    @Mapping(target = "accountId", source = "account.id")
    MemberResponse toMemberResponse(Member entity);

    void updateMember(@MappingTarget Member entity, MemberUpdateRequest request);
}
