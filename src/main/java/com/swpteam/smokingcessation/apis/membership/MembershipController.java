package com.swpteam.smokingcessation.apis.membership;

import com.swpteam.smokingcessation.apis.membership.dto.MembershipCreationRequest;
import com.swpteam.smokingcessation.apis.membership.dto.MembershipUpdateRequest;
import com.swpteam.smokingcessation.apis.membership.dto.MembershipResponse;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constants.SuccessCode;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MembershipController {
    MemberService memberService;

    @GetMapping
    ResponseEntity<ApiResponse<Page<MembershipResponse>>> getMembershipPage(@Valid PageableRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<Page<MembershipResponse>>builder()
                        .result(memberService.getMembershipPage(request))
                        .build()
        );
    }

    @GetMapping("/{name}")
    ResponseEntity<ApiResponse<MembershipResponse>> getMembershipByName(@PathVariable String membershipName) {
        return ResponseEntity.ok(
                ApiResponse.<MembershipResponse>builder()
                        .result(memberService.getMembershipByName(membershipName))
                        .build()
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<MembershipResponse>> getMembershipById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponse.<MembershipResponse>builder()
                        .result(memberService.getMembershipById(id))
                        .build()
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<MembershipResponse>> createMembership(@RequestBody @Valid MembershipCreationRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<MembershipResponse>builder()
                        .code(SuccessCode.MEMBERSHIP_CREATED.getCode())
                        .message(SuccessCode.MEMBERSHIP_CREATED.getMessage())
                        .result(memberService.createMembership(request))
                        .build()
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<MembershipResponse>> updateMembership(@PathVariable String id, @RequestBody @Valid MembershipUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<MembershipResponse>builder()
                        .code(SuccessCode.MEMBERSHIP_UPDATED.getCode())
                        .message(SuccessCode.MEMBERSHIP_UPDATED.getMessage())
                        .result(memberService.updateMembership(id, request))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> deleteMembership(@PathVariable String id) {
        memberService.deleteMembership(id);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .code(SuccessCode.MEMBERSHIP_DELETED.getCode())
                        .message(SuccessCode.MEMBERSHIP_DELETED.getMessage())
                        .build()
        );
    }
}
