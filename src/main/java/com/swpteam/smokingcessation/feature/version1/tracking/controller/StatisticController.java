package com.swpteam.smokingcessation.feature.version1.tracking.controller;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.statistics.AdminStatisticResponse;
import com.swpteam.smokingcessation.domain.dto.statistics.MemberStatisticResponse;
import com.swpteam.smokingcessation.feature.version1.tracking.service.IStatisticService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Statistics", description = "Manage statistic-related operations")
public class StatisticController {
    ResponseUtilService responseUtilService;
    IStatisticService statisticService;

    @GetMapping("/member")
    ResponseEntity<ApiResponse<MemberStatisticResponse>> getMemberStatistics() {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.STATISTICS_FETCHED_BY_ACCOUNT,
                statisticService.getMemberStatistics()
        );
    }

    @GetMapping("member/current-month")
    ResponseEntity<ApiResponse<MemberStatisticResponse>> GetCurrentMonthMemberStatistics() {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.STATISTICS_FETCHED_BY_ACCOUNT,
                statisticService.getCurrentMonthMemberStatistics()
        );
    }

    @GetMapping("/admin")
    ResponseEntity<ApiResponse<AdminStatisticResponse>> getAdminStatistics() {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.STATISTICS_FETCHED_BY_ACCOUNT,
                statisticService.getAdminStatistics()
        );
    }

    @GetMapping("/admin/current-month")
    ResponseEntity<ApiResponse<AdminStatisticResponse>> getCurrentMonthAdminStatistics() {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.STATISTICS_FETCHED_BY_ACCOUNT,
                statisticService.getCurrentMonthAdminStatistics()
        );
    }
}
