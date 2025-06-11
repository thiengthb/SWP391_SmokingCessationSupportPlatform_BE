package com.swpteam.smokingcessation.apis.membership;

import com.swpteam.smokingcessation.apis.membership.dto.MembershipRequest;
import com.swpteam.smokingcessation.apis.membership.dto.MembershipResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MembershipMapper {

    Membership toEntity(MembershipRequest request);

    MembershipResponse toResponse(Membership membership);

    void update(@MappingTarget Membership membership, MembershipRequest request);
}
