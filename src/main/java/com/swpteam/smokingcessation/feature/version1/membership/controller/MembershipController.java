package com.swpteam.smokingcessation.feature.version1.membership.controller;

import com.swpteam.smokingcessation.domain.dto.membership.MembershipCreateRequest;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipCurrencyUpdateRequest;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipResponse;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipUpdateRequest;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.feature.version1.membership.service.IMembershipService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/memberships")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Membership", description = "Manage membership-related operations")
public class MembershipController {

    IMembershipService membershipService;
    ResponseUtilService responseUtilService;

    @GetMapping
    ResponseEntity<ApiResponse<List<MembershipResponse>>> getMembershipList() {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MEMBERSHIP_LIST_FETCHED,
                membershipService.getMembershipList()
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<MembershipResponse>> getMembershipById(
            @PathVariable String id
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MEMBERSHIP_FETCHED_BY_ID,
                membershipService.getMembershipById(id)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<MembershipResponse>> createMembership(
            @RequestBody @Valid MembershipCreateRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MEMBERSHIP_CREATED,
                membershipService.createMembership(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<MembershipResponse>> updateMembership(
            @PathVariable String id,
            @RequestBody @Valid MembershipUpdateRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MEMBERSHIP_UPDATED,
                membershipService.updateMembership(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteMembership(
            @PathVariable String id
    ) {
        membershipService.softDeleteMembershipById(id);
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MEMBERSHIP_DELETED
        );
    }

    @PutMapping("/currency/{id}")
    ResponseEntity<ApiResponse<MembershipResponse>> updateMembershipCurrency(
            @PathVariable String id,
            @RequestBody @Valid MembershipCurrencyUpdateRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MEMBERSHIP_UPDATED,
                membershipService.updateMembershipCurrency(id, request)
        );
    }
}
