package com.swpteam.smokingcessation.controller.test;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/test")
@Tag(name = "Testing", description = "For testing services")
public class TestController {

    AuthUtilService authUtilService;

    @PostMapping
    ResponseEntity<ApiResponse<Object>> createAccount(@RequestBody @Valid TestRequest request) {

        Account result = authUtilService.getCurrentAccountOrThrowError();

        return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                        .code(200)
                        .message("Success")
                        .result(result)
                        .build());
    }
}
