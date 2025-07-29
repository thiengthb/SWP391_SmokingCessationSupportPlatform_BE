package com.swpteam.smokingcessation.repository.report;

import com.swpteam.smokingcessation.domain.dto.report.*;

import java.time.LocalDateTime;
import java.util.List;

public interface IReportRepository {
    ReportSummaryResponse getReportSummary(LocalDateTime from, LocalDateTime to);

    List<UserActivityResponse> getUserActivity(LocalDateTime from, LocalDateTime to);

    UserDistributionResponse getUserDistribution();

    List<RevenueResponse> getRevenue(LocalDateTime from, LocalDateTime to);

    List<CompletionResponse> getCompletetion(LocalDateTime from, LocalDateTime to);

    PremiumDistributionResponse getPremiumDistribution();
}
