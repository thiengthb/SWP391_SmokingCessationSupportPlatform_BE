package com.swpteam.smokingcessation.feature.version1.profile.controller;

import com.swpteam.smokingcessation.domain.dto.member.MemberCreateRequest;
import com.swpteam.smokingcessation.domain.dto.member.MemberProfileResponse;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.member.MemberUpdateRequest;
import com.swpteam.smokingcessation.domain.dto.member.ProgressResponse;
import com.swpteam.smokingcessation.feature.version1.profile.service.IMemberService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Member", description = "Manage member-related operations")
public class MemberController {

    IMemberService memberService;
    ResponseUtilService responseUtilService;

    @GetMapping("/my-profile")
    ResponseEntity<ApiResponse<MemberProfileResponse>> getMyMemberProfile() {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MEMBER_FETCHED_BY_ID,
                memberService.getMyMemberProfile()
        );
    }

    @GetMapping("/{accountId}")
    ResponseEntity<ApiResponse<MemberProfileResponse>> getMemberById(
            @PathVariable String accountId
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MEMBER_FETCHED_BY_ID,
                memberService.getMemberById(accountId)
        );
    }

    @GetMapping("/progress")
    ResponseEntity<ApiResponse<ProgressResponse>> getProgress(){
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MEMBER_FETCHED_BY_ID,
                memberService.getProgress()
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<MemberProfileResponse>> createMember(
            @RequestBody @Valid MemberCreateRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MEMBER_CREATED,
                memberService.createMember(request)
        );
    }

    @PutMapping("/{accountId}")
    ResponseEntity<ApiResponse<MemberProfileResponse>> updateMemberById(
            @PathVariable String accountId,
            @RequestBody MemberUpdateRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MEMBER_UPDATED,
                memberService.updateMemberById(accountId, request)
        );
    }

    @PutMapping("/my-profile")
    ResponseEntity<ApiResponse<MemberProfileResponse>> updateMyMemberProfile(
            @RequestBody MemberUpdateRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MEMBER_UPDATED,
                memberService.updateMyMemberProfile(request)
        );
    }
    
}
