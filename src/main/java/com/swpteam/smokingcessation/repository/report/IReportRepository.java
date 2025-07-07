package com.swpteam.smokingcessation.repository.report;

import com.swpteam.smokingcessation.domain.dto.report.UserActivityResponse;
import com.swpteam.smokingcessation.domain.dto.report.ReportSummaryResponse;
import com.swpteam.smokingcessation.domain.dto.report.UserDistributionResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface IReportRepository {
    ReportSummaryResponse getReportSummary(LocalDateTime from, LocalDateTime to);

    List<UserActivityResponse> getUserActivity(LocalDateTime from, LocalDateTime to);

    UserDistributionResponse getUserDistribution();
}
