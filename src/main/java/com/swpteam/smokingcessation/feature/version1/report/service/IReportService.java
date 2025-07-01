package com.swpteam.smokingcessation.feature.version1.report.service;

import com.swpteam.smokingcessation.domain.dto.report.ReportSummaryRequest;
import com.swpteam.smokingcessation.domain.dto.report.ReportSummaryResponse;

public interface IReportService {
    ReportSummaryResponse getSummary(ReportSummaryRequest reportSummaryRequest);
}
