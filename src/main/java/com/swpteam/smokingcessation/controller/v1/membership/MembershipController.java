package com.swpteam.smokingcessation.controller.v1.membership;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipCreateRequest;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipCurrencyUpdateRequest;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipResponse;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipUpdateRequest;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.service.interfaces.membership.IMembershipService;
import com.swpteam.smokingcessation.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/memberships")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Membership", description = "Manage membership-related operations")
public class MembershipController {

    IMembershipService membershipService;

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<MembershipResponse>>> getMembershipPage(
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.MEMBERSHIP_GET_ALL,
                membershipService.getMembershipPage(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<MembershipResponse>> getMembershipById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.MEMBERSHIP_GET_BY_ID,
                membershipService.getMembershipById(id)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<MembershipResponse>> createMembership(
            @RequestBody @Valid MembershipCreateRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.MEMBERSHIP_CREATED,
                membershipService.createMembership(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<MembershipResponse>> updateMembership(
            @PathVariable String id,
            @RequestBody @Valid MembershipUpdateRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.MEMBERSHIP_UPDATED,
                membershipService.updateMembership(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> deleteMembership(
            @PathVariable String id
    ) {
        membershipService.softDeleteMembershipById(id);
        return ResponseUtil.buildResponse(
                SuccessCode.MEMBERSHIP_DELETED,
                null
        );
    }

    @PutMapping("/currency/{id}")
    ResponseEntity<ApiResponse<MembershipResponse>> updateMembershipCurrency(
            @PathVariable String id,
            @RequestBody @Valid MembershipCurrencyUpdateRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.MEMBERSHIP_UPDATED,
                membershipService.updateMembershipCurrency(id, request)
        );
    }
}
