package com.swpteam.smokingcessation.apis.member.controller;

import com.swpteam.smokingcessation.apis.member.dto.request.MemberCreateRequest;
import com.swpteam.smokingcessation.apis.member.dto.response.MemberResponse;
import com.swpteam.smokingcessation.apis.member.service.MemberService;
import com.swpteam.smokingcessation.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MemberController {
    MemberService memberService;

    @PostMapping("/create/{accountId}")
    ApiResponse<MemberResponse> createMember(@PathVariable("accountId") String accountId, @RequestBody @Valid MemberCreateRequest request) {
        var result = memberService.createMember(accountId,request);

        return ApiResponse.<MemberResponse>builder()
                .result(result)
                .build();
    }


}
