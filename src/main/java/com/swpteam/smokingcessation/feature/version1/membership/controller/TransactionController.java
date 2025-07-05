package com.swpteam.smokingcessation.feature.version1.membership.controller;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.transaction.TransactionListItemResponse;
import com.swpteam.smokingcessation.domain.dto.transaction.TransactionResponse;
import com.swpteam.smokingcessation.feature.version1.membership.service.ITransactionService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Transaction", description = "Manage transaction-related operations")
public class TransactionController {

    ITransactionService transactionService;
    ResponseUtilService responseUtilService;

    @GetMapping("/my-transactions")
    ResponseEntity<ApiResponse<PageResponse<TransactionListItemResponse>>> getMyTransactionsPage(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.TRANSACTION_FETCH_BY_ACCOUNT,
                transactionService.getMyTransactionsPage(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<TransactionResponse>> getTransactionById(
            @PathVariable String id
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.TRANSACTION_FETCH_BY_ID,
                transactionService.getTransactionById(id)
        );
    }
}


