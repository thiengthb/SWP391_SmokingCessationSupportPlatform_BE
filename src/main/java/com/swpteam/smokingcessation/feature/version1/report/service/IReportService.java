package com.swpteam.smokingcessation.feature.version1.report.service;

import com.swpteam.smokingcessation.domain.dto.report.UserActivityResponse;
import com.swpteam.smokingcessation.domain.dto.report.ReportSummaryRequest;
import com.swpteam.smokingcessation.domain.dto.report.ReportSummaryResponse;
import com.swpteam.smokingcessation.domain.dto.report.UserDistributionResponse;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface IReportService {
    ReportSummaryResponse getSummary(ReportSummaryRequest reportSummaryRequest);

    List<UserActivityResponse> getUserGrowth(ReportSummaryRequest request);

    UserDistributionResponse getUserDistribution();
}
