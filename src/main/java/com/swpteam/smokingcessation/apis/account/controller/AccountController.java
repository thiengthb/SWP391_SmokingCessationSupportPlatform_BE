package com.swpteam.smokingcessation.apis.account.controller;

import com.swpteam.smokingcessation.apis.account.dto.request.AccountCreateRequest;
import com.swpteam.smokingcessation.apis.account.dto.request.AccountUpdateRequest;
import com.swpteam.smokingcessation.apis.account.dto.request.ChangePasswordRequest;
import com.swpteam.smokingcessation.apis.account.dto.response.AccountResponse;
import com.swpteam.smokingcessation.apis.account.entity.Account;
import com.swpteam.smokingcessation.apis.account.service.AccountService;
import com.swpteam.smokingcessation.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/account")
class AccountController {
    AccountService accountService;

    @PostMapping("/create")
    ApiResponse<AccountResponse> createAccount(@RequestBody @Valid AccountCreateRequest request) {
        var result = accountService.createAccount(request);

        return ApiResponse.<AccountResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/change-password")
    ApiResponse<AccountResponse> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        var result = accountService.changePassword(request);

        return ApiResponse.<AccountResponse>builder()
                .result(result)
                .build();
    }

    @GetMapping("/me")
    ApiResponse<AccountResponse> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        AccountResponse result = accountService.getAccountByEmail(email);
        return ApiResponse.<AccountResponse>builder()
                .result(result)
                .build();
    }

    @PutMapping("/update/{userId}")
    ApiResponse<AccountResponse> updateAccount(@PathVariable("userId") String id, @RequestBody AccountUpdateRequest request) {
        var result = accountService.updateAccount(request, id);

        return ApiResponse.<AccountResponse>builder()
                .result(result)
                .build();
    }

    @DeleteMapping("/delete/{userId}")
    ApiResponse<String> deleteAccount(@PathVariable("userId") String id) {
        accountService.deleteAccount(id);

        return ApiResponse.<String>builder()
                .result("User is deleted")
                .build();
    }

    @GetMapping
    List<Account> getUsers() {
        return accountService.getAccounts();
    }

    @GetMapping("/{userId}")
    ApiResponse<AccountResponse> getAccountById(@PathVariable("userId") String id) {
        var result = accountService.getAccountById(id);

        return ApiResponse.<AccountResponse>builder()
                .result(result)
                .build();
    }
}
