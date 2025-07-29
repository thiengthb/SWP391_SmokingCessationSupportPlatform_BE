package com.swpteam.smokingcessation.feature.version1.report.controller;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.report.*;
import com.swpteam.smokingcessation.feature.version1.report.service.IReportService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Report", description = "Manage report-related operations")
public class ReportController {

    IReportService reportService;
    ResponseUtilService responseUtilService;

    @GetMapping("/summary")
    ResponseEntity<ApiResponse<ReportSummaryResponse>> getReportSummary(
            @Valid ReportSummaryRequest reportSummaryRequest
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.REPORT_SUMMARY_FETCHED,
                reportService.getSummary(reportSummaryRequest)
        );
    }

    @GetMapping("/user-growth")
    ResponseEntity<ApiResponse<List<UserActivityResponse>>> getUserGrowth(
            @Valid ReportSummaryRequest reportSummaryRequest
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.USER_GROWTH_FETCHED,
                reportService.getUserGrowth(reportSummaryRequest)
        );
    }

    @GetMapping("user-distribution")
    ResponseEntity<ApiResponse<UserDistributionResponse>> getUserDistribution(
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.USER_DISTRIBUTION_FETCHED,
                reportService.getUserDistribution()
        );
    }

    @GetMapping("revenue")
    ResponseEntity<ApiResponse<List<RevenueResponse>>> getRevenue(
            @Valid ReportSummaryRequest reportSummaryRequest
    ){
        return responseUtilService.buildSuccessResponse(
                SuccessCode.REVENUE_FETCHED,
                reportService.getRevenue(reportSummaryRequest)
        );
    }

    @GetMapping("premium-distribution")
    ResponseEntity<ApiResponse<PremiumDistributionResponse>> getPremiumDistribution(
    ){
        return responseUtilService.buildSuccessResponse(
                SuccessCode.PREMIUM_DISTRIBUTION_FETCHED,
                reportService.getPremiumDistribution()
        );
    }

    @GetMapping("completion")
    ResponseEntity<ApiResponse<List<CompletionResponse>>> getCompletion(
            @Valid ReportSummaryRequest reportSummaryRequest
    )
    {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.COMPLETION_RATE_FETCHED,
                reportService.getCompletion(reportSummaryRequest)
        );
    }
}
