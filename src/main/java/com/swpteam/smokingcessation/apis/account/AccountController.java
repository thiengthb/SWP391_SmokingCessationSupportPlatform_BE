package com.swpteam.smokingcessation.apis.account;

import com.swpteam.smokingcessation.apis.account.dto.AccountCreateRequest;
import com.swpteam.smokingcessation.apis.account.dto.AccountResponse;
import com.swpteam.smokingcessation.apis.account.dto.AccountUpdateRequest;
import com.swpteam.smokingcessation.apis.account.dto.ChangePasswordRequest;
import com.swpteam.smokingcessation.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/accounts")
class AccountController {
    AccountService accountService;

    @PostMapping
    ApiResponse<AccountResponse> createAccount(@RequestBody @Valid AccountCreateRequest request) {
        var result = accountService.createAccount(request);

        return ApiResponse.<AccountResponse>builder()
                .result(result)
                .build();
    }

    @GetMapping
    ResponseEntity<ApiResponse<List<AccountResponse>>> getUsers() {
        return ResponseEntity.ok(
                ApiResponse.<List<AccountResponse>>builder()
                        .result(accountService.getAccounts())
                        .build());
    }

    @GetMapping("/{id}")
    ApiResponse<AccountResponse> getAccountById(@PathVariable("id") String id) {
        var result = accountService.getAccountById(id);

        return ApiResponse.<AccountResponse>builder()
                .result(result)
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<AccountResponse> updateAccount(@PathVariable("id") String id, @RequestBody AccountUpdateRequest request) {
        var result = accountService.updateAccount(request, id);

        return ApiResponse.<AccountResponse>builder()
                .result(result)
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteAccount(@PathVariable("id") String id) {
        accountService.deleteAccount(id);

        return ApiResponse.<String>builder()
                .result("User is deleted")
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

}
