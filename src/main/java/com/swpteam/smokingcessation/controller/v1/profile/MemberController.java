package com.swpteam.smokingcessation.controller.v1.profile;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.domain.dto.member.MemberRequest;
import com.swpteam.smokingcessation.domain.dto.member.MemberResponse;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.service.interfaces.profile.IMemberService;
import com.swpteam.smokingcessation.utils.ResponseUtil;
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

    @PostMapping
    ResponseEntity<ApiResponse<MemberResponse>> createMember(
            @RequestBody @Valid MemberRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.MEMBER_CREATED,
                memberService.createMember(request)
        );
    }

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<MemberResponse>>> getUsers(
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.HEALTH_DELETED,
                memberService.getMembersPage(request)
        );
    }

    @GetMapping("/{accountId}")
    ResponseEntity<ApiResponse<MemberResponse>> getMemberById(
            @PathVariable String accountId
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.HEALTH_DELETED,
                memberService.getMemberById(accountId)
        );
    }

    @PutMapping("/{accountId}")
    ResponseEntity<ApiResponse<MemberResponse>> updateAccount(
            @PathVariable String accountId,
            @RequestBody MemberRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.ACCOUNT_UPDATED,
                memberService.updateMemberById(accountId, request)
        );
    }
    
}
