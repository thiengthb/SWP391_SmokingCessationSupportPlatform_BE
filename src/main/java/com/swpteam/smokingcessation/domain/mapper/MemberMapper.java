package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.member.MemberProfileResponse;
import com.swpteam.smokingcessation.domain.dto.member.MemberRequest;
import com.swpteam.smokingcessation.domain.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "score", ignore = true)
    @Mapping(target = "highestStreak", ignore = true)
    @Mapping(target = "lastCounterReset", ignore = true)
    Member toEntity(MemberRequest request);

    @Mapping(target = "id", source = "account.id")
    @Mapping(target = "username", source = "account.username")
    @Mapping(target = "email", source = "account.email")
    @Mapping(target = "phoneNumber", source = "account.phoneNumber")
    @Mapping(target = "avatar", source = "account.avatar")
    MemberProfileResponse toResponse(Member entity);

    void updateMember(@MappingTarget Member entity, MemberRequest request);
}
