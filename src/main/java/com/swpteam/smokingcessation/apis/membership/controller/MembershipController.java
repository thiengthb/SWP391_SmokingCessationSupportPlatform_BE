package com.swpteam.smokingcessation.apis.membership.controller;

import com.swpteam.smokingcessation.apis.membership.dto.request.MembershipCreationRequest;
import com.swpteam.smokingcessation.apis.membership.dto.request.MembershipUpdateRequest;
import com.swpteam.smokingcessation.apis.membership.dto.response.MembershipResponse;
import com.swpteam.smokingcessation.apis.membership.service.MemberService;
import com.swpteam.smokingcessation.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MembershipController {
    MemberService memberService;

    @PostMapping
    ApiResponse<MembershipResponse> createMembership(@RequestBody @Valid MembershipCreationRequest request) {
        return ApiResponse.<MembershipResponse>builder()
                .result(memberService.createMembership(request))
                .build();
    }

    @PutMapping("/{membershipName}")
    ApiResponse<MembershipResponse> updateMembership(@PathVariable String membershipName, @RequestBody @Valid MembershipUpdateRequest request) {
        return ApiResponse.<MembershipResponse>builder()
                .result(memberService.updateMembership(membershipName, request))
                .build();
    }

    @DeleteMapping("/{membershipName}")
    ApiResponse<String> createMembership(@PathVariable String membershipName) {
        memberService.deleteMembership(membershipName);
        return ApiResponse.<String>builder()
                .result("Membership has been deleted")
                .build();
    }

    @GetMapping
    ApiResponse<List<MembershipResponse>> getMembershipList() {
        return ApiResponse.<List<MembershipResponse>>builder()
                .result(memberService.getMembershipList())
                .build();
    }

    @GetMapping("/{membershipName}")
    ApiResponse<MembershipResponse> getMembershipList(@PathVariable String membershipName) {
        return ApiResponse.<MembershipResponse>builder()
                .result(memberService.getMembership(membershipName))
                .build();
    }
}
