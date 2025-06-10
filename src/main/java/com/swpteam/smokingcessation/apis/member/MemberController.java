package com.swpteam.smokingcessation.apis.member;

import com.swpteam.smokingcessation.apis.member.dto.MemberCreateRequest;
import com.swpteam.smokingcessation.apis.member.dto.MemberResponse;
import com.swpteam.smokingcessation.apis.member.dto.MemberUpdateRequest;
import com.swpteam.smokingcessation.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MemberController {
    MemberService memberService;

    @PostMapping("/create/{accountId}")
    ApiResponse<MemberResponse> createMember(@PathVariable("accountId") String accountId, @RequestBody @Valid MemberCreateRequest request) {
        var result = memberService.createMember(request, accountId);

        return ApiResponse.<MemberResponse>builder()
                .result(result)
                .build();
    }

    @GetMapping
    List<Member> getMembers() {
        return memberService.getMembers();
    }

    @GetMapping("/{accountId}")
    ApiResponse<MemberResponse> getMemberById(@PathVariable("accountId") String id) {
        var result = memberService.getMemberById(id);

        return ApiResponse.<MemberResponse>builder()
                .result(result)
                .build();
    }

    @PutMapping("/update/{accountId}")
    ApiResponse<MemberResponse> updateAccount(@PathVariable("accountId") String id, @RequestBody MemberUpdateRequest request) {
        var result = memberService.updateMember(request, id);

        return ApiResponse.<MemberResponse>builder()
                .result(result)
                .build();
    }
}
