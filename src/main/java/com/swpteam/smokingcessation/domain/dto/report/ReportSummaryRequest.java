package com.swpteam.smokingcessation.domain.dto.report;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDateTime;

public record ReportSummaryRequest (
    @Past(message = "REPORT_FROM_DATE_INVALID")
    @NotNull(message = "REPORT_FROM_DATE_REQUIRED")
    LocalDateTime from,

    @Past(message = "REPORT_TO_DATE_INVALID")
    LocalDateTime to
) {}
