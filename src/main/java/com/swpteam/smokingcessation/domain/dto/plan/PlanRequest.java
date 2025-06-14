package com.swpteam.smokingcessation.domain.dto.plan;

import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRequest {

    @NotBlank(message = "ACCOUNT_ID_REQUIRED")
    String accountId;

    @NotBlank(message = "PLANE_NAME_REQUIRED")
    @Size(max = 255, message = "PLAN_NAME_TOO_LONG")
    String planName;

    @NotBlank(message = "PLAN_DESCRIPTION_REQUIRED")
    @Size(max = 255, message = "PLAN_DESCRIPTION_TOO_LONG")
    String description;

    @NotNull(message ="SUCCESS_RATE_REQUIRED")
    @Min(value = 0, message = "SUCCESS_LEVEL_INVALID_MIN")
    @Max(value = 11, message = "SUCCESS_LEVEL_INVALID_MAX")
    double successRate;

    @NotNull(message = "PLAN_START_DATE_REQUIRED")
    @FutureOrPresent(message = "PLAN_START_DATE_MUST_BE_TODAY_OR_FUTURE")
    LocalDate startDate;

    @NotNull(message = "PLAN_END_DATE_REQUIRED")
    @Future(message = "PLAN_END_DATE_MUST_BE_IN_FUTURE")
    LocalDate endDate;

    @NotNull(message = "PLAN_STATUS_REQUIRED")
    PlanStatus planStatus;
}
