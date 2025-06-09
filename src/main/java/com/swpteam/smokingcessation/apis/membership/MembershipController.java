package com.swpteam.smokingcessation.apis.membership;

import com.swpteam.smokingcessation.apis.membership.dto.MembershipCreationRequest;
import com.swpteam.smokingcessation.apis.membership.dto.MembershipUpdateRequest;
import com.swpteam.smokingcessation.apis.membership.dto.MembershipResponse;
import com.swpteam.smokingcessation.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MembershipController {
    MemberService memberService;

    @PostMapping
    ApiResponse<MembershipResponse> createMembership(@RequestBody @Valid MembershipCreationRequest request) {
        return ApiResponse.<MembershipResponse>builder()
                .message("Membership has been created")
                .result(memberService.createMembership(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<MembershipResponse> updateMembership(@PathVariable String id, @RequestBody @Valid MembershipUpdateRequest request) {
        return ApiResponse.<MembershipResponse>builder()
                .message("Membership has been updated")
                .result(memberService.updateMembership(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> createMembership(@PathVariable String id) {
        memberService.deleteMembership(id);
        return ApiResponse.<String>builder()
                .message("Membership has been deleted")
                .build();
    }

    @GetMapping
    ApiResponse<List<MembershipResponse>> getMembershipList() {
        return ApiResponse.<List<MembershipResponse>>builder()
                .result(memberService.getMembershipList())
                .build();
    }

    @GetMapping("/{name}")
    ApiResponse<MembershipResponse> getMembershipByName(@PathVariable String membershipName) {
        return ApiResponse.<MembershipResponse>builder()
                .result(memberService.getMembershipByName(membershipName))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<MembershipResponse> getMembershipById(@PathVariable String id) {
        return ApiResponse.<MembershipResponse>builder()
                .result(memberService.getMembershipById(id))
                .build();
    }
}
