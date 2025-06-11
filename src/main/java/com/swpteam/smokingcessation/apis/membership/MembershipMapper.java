package com.swpteam.smokingcessation.apis.membership;

import com.swpteam.smokingcessation.apis.membership.dto.MembershipCreateRequest;
import com.swpteam.smokingcessation.apis.membership.dto.MembershipResponse;
import com.swpteam.smokingcessation.apis.membership.dto.MembershipUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MembershipMapper {

    Membership toEntity(MembershipCreateRequest request);

    MembershipResponse toResponse(Membership membership);

    void update(@MappingTarget Membership membership, MembershipUpdateRequest request);
}
