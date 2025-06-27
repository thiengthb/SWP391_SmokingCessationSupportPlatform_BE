package com.swpteam.smokingcessation.controller.v1.identity;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.account.AccountRequest;
import com.swpteam.smokingcessation.domain.dto.account.AccountResponse;
import com.swpteam.smokingcessation.domain.dto.account.AccountUpdateRequest;
import com.swpteam.smokingcessation.domain.dto.account.ChangePasswordRequest;
import com.swpteam.smokingcessation.domain.enums.Role;
import com.swpteam.smokingcessation.service.interfaces.identity.IAccountService;
import com.swpteam.smokingcessation.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Account", description = "Manage account-related operations")
public class AccountController {

    IAccountService accountService;

    @PostMapping
    ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @RequestBody @Valid AccountRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.ACCOUNT_CREATED,
                accountService.createAccount(request)
        );
    }

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<AccountResponse>>> getAccounts(@Valid PageableRequest request) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.ACCOUNT_CREATED,
                accountService.getAccountsPage(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<AccountResponse>> getAccountById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.ACCOUNT_CREATED,
                accountService.getAccountById(id)
        );
    }

    @PutMapping("/{id}/role")
    ResponseEntity<ApiResponse<AccountResponse>> updateAccountRole(
            @PathVariable String id,
            @RequestParam Role role
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.ROLE_UPDATED,
                accountService.updateAccountRole(id, role)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<AccountResponse>> updateAccount(
            @PathVariable String id,
            @RequestBody AccountUpdateRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.ACCOUNT_UPDATED,
                accountService.updateAccountWithoutRole(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable String id) {
        accountService.deleteAccount(id);
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.ACCOUNT_DELETED
        );
    }

    @PostMapping("/change-password")
    ResponseEntity<ApiResponse<AccountResponse>> changePassword(
            @RequestBody @Valid ChangePasswordRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.PASSWORD_CHANGE_SUCCESS,
                accountService.changePassword(request)
        );
    }

    @GetMapping("/me")
    ResponseEntity<ApiResponse<AccountResponse>> getMe() {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.GET_ME,
                accountService.getCurrentAccount()
        );
    }

    @PutMapping("/ban/{id}")
    ResponseEntity<ApiResponse<Void>> banAccount(
            @PathVariable String id
    ) {
        accountService.banAccount(id);
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.ACCOUNT_BANNED
        );
    }

}
