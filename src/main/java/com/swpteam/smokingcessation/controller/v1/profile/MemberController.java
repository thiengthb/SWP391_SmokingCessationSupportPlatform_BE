package com.swpteam.smokingcessation.controller.v1.profile;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.domain.dto.member.MemberRequest;
import com.swpteam.smokingcessation.domain.dto.member.MemberResponse;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.service.interfaces.profile.IMemberService;
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

    @GetMapping("/{accountId}")
    ResponseEntity<ApiResponse<MemberResponse>> getMemberById(
            @PathVariable String accountId
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MEMBER_FETCHED_BY_ID,
                memberService.getMemberById(accountId)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<MemberResponse>> createMember(
            @RequestBody @Valid MemberRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MEMBER_CREATED,
                memberService.createMember(request)
        );
    }

    @PutMapping("/{accountId}")
    ResponseEntity<ApiResponse<MemberResponse>> updateMemberById(
            @PathVariable String accountId,
            @RequestBody MemberRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MEMBER_UPDATED,
                memberService.updateMemberById(accountId, request)
        );
    }
    
}
