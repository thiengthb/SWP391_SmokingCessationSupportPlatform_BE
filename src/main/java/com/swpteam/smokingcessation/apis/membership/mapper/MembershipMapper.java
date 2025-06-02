package com.swpteam.smokingcessation.apis.membership.mapper;

import com.swpteam.smokingcessation.apis.membership.dto.request.MembershipCreationRequest;
import com.swpteam.smokingcessation.apis.membership.dto.request.MembershipUpdateRequest;
import com.swpteam.smokingcessation.apis.membership.dto.response.MembershipResponse;
import com.swpteam.smokingcessation.apis.membership.entity.Membership;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MembershipMapper {
    Membership toEntity(MembershipCreationRequest request);

    MembershipResponse toResponse(Membership membership);

    void updateMembership(@MappingTarget Membership membership, MembershipUpdateRequest request);
}
