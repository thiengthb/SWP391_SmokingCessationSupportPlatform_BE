package com.swpteam.smokingcessation.apis.account;

import com.swpteam.smokingcessation.apis.account.dto.AccountRequest;
import com.swpteam.smokingcessation.apis.account.dto.AccountResponse;
import com.swpteam.smokingcessation.apis.account.dto.ChangePasswordRequest;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constants.SuccessCode;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/accounts")
class AccountController {
    AccountService accountService;

    @PostMapping
    ResponseEntity<ApiResponse<AccountResponse>> createAccount(@RequestBody @Valid AccountRequest request) {
        var result = accountService.createAccount(request);

        return ResponseEntity.ok(
                ApiResponse.<AccountResponse>builder()
                        .code(SuccessCode.ACCOUNT_CREATED.getCode())
                        .message(SuccessCode.ACCOUNT_CREATED.getMessage())
                        .result(result)
                        .build());
    }

    @GetMapping
    ResponseEntity<ApiResponse<Page<AccountResponse>>> getUsers(@Valid PageableRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<Page<AccountResponse>>builder()
                        .result(accountService.getAccounts(request))
                        .build());
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<AccountResponse>> getAccountById(@PathVariable("id") String id) {
        return ResponseEntity.ok(
                ApiResponse.<AccountResponse>builder()
                        .result(accountService.getAccountById(id))
                        .build());
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<AccountResponse>> updateAccount(@PathVariable("id") String id, @RequestBody AccountRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<AccountResponse>builder()
                        .code(SuccessCode.ACCOUNT_UPDATED.getCode())
                        .message(SuccessCode.ACCOUNT_UPDATED.getMessage())
                        .result(accountService.updateAccount(id, request))
                        .build());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> deleteAccount(@PathVariable("id") String id) {
        accountService.deleteAccount(id);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .code(SuccessCode.ACCOUNT_DELETED.getCode())
                        .message(SuccessCode.ACCOUNT_DELETED.getMessage())
                        .build());
    }

    @PostMapping("/change-password")
    ResponseEntity<ApiResponse<AccountResponse>> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<AccountResponse>builder()
                        .code(SuccessCode.PASSWORD_CHANGE_SUCCESS.getCode())
                        .message(SuccessCode.PASSWORD_CHANGE_SUCCESS.getMessage())
                        .result(accountService.changePassword(request))
                        .build());
    }

    @GetMapping("/me")
    ResponseEntity<ApiResponse<AccountResponse>> getMe(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(
                ApiResponse.<AccountResponse>builder()
                        .result(accountService.getAccountByEmail(email))
                        .build());
    }

    //@PostMapping("/ban/{id}")
    //ResponseEntity<ApiResponse<AccountResponse>> banAccount() {

}
