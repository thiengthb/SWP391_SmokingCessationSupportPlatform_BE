package com.swpteam.smokingcessation.apis.member;

import com.swpteam.smokingcessation.apis.member.dto.MemberCreateRequest;
import com.swpteam.smokingcessation.apis.member.dto.MemberResponse;
import com.swpteam.smokingcessation.apis.member.dto.MemberUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "score", ignore = true)
    @Mapping(target = "currentStreak", ignore = true)
    @Mapping(target = "lastCounterReset", ignore = true)
    Member toMember(MemberCreateRequest request);

    @Mapping(target = "id", source = "account.id")
    MemberResponse toMemberResponse(Member entity);

    void updateMember(@MappingTarget Member entity, MemberUpdateRequest request);
}
