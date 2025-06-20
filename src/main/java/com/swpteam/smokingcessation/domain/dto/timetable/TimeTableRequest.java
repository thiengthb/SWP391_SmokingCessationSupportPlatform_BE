package com.swpteam.smokingcessation.domain.dto.timetable;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;


public record TimeTableRequest (
    @NotBlank(message = "TIMETABLE_NAME_REQUIRED")
    String name,

    @NotBlank(message = "TIMETABLE_DESCRIPTION_REQUIRED")
    String description,

    @NotNull(message = "STARTED_AT_REQUIRED")
    @FutureOrPresent(message = "STARTED_AT_MUST_BE_TODAY_OR_FUTURE")
    LocalDateTime startedAt,

    @NotNull(message = "ENDED_AT_REQUIRED")
    @Future(message = "ENDED_AT_MUST_BE_IN_FUTURE")
    LocalDateTime endedAt
    ) {}




